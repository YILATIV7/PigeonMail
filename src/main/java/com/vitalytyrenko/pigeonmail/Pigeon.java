package com.vitalytyrenko.pigeonmail;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class Pigeon implements Comparable<Pigeon>, Cloneable, Visualizable {

    /*
    MoveType:
        0 - звичайний рух - голуб відштовхується від інших голубів
        1 - режим "екстра" - голуб червоніє і починає поїдати тих, з якими стикається
     */

    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int MOVE_TYPE_STANDARD;
    public static final int MOVE_TYPE_CUSTOM;
    public static final String UNNAMED;

    static {
        WIDTH = 75;
        HEIGHT = 75;
        MOVE_TYPE_STANDARD = 0;
        MOVE_TYPE_CUSTOM = 1;
        UNNAMED = "безіменний";
    }

    private boolean selected;
    private boolean deleted;
    private double x, y;
    private Vector moveVector;  // посилальний тип, для якого потрібно застосувати глибинне копіювання
    private int moveType;
    private String name;

    private final Node root;
    private final Label label;
    private final ImageView imageView;

    {
        selected = false;
        deleted = false;
    }

    public Pigeon(double x, double y, int degrees, int moveType, String name) {
        this.x = x;
        this.y = y;
        this.moveType = moveType;
        this.name = name;
        setDegrees(degrees);

        imageView = new ImageView();
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        label = new Label();
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setText(name);

        Rectangle rect = new Rectangle(0, 0, Color.WHITE);
        label.widthProperty().addListener((v, t1, t12) -> {
            double posX = (Pigeon.WIDTH - label.getWidth()) / 2.0;
            rect.setWidth(label.getWidth());
            rect.setX(posX);
            label.setTranslateX(posX);
        });
        label.heightProperty().addListener((v, t1, t2) -> rect.setHeight(label.getHeight()));

        root = new AnchorPane(imageView, rect, label);
    }

    public Pigeon() {
        this(
                Universal.RANDOMIZER.nextInt(Universal.WIDTH - Pigeon.WIDTH),
                Universal.RANDOMIZER.nextInt(Universal.HEIGHT - Pigeon.HEIGHT),
                Universal.RANDOMIZER.nextInt(360),
                0,
                UNNAMED
        );
    }

    // getters-setters

    public void setDegrees(int degrees) {
        moveVector = new Vector(degrees);
    }

    public Vector getMoveVector() {
        return moveVector;
    }

    public void setMoveVector(Vector moveVector) {
        this.moveVector = moveVector;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getMoveType() {
        return moveType;
    }

    public void setMoveType(int moveType) {
        this.moveType = moveType;
    }

    public void swapMoveType() {
        if (moveType == MOVE_TYPE_STANDARD) moveType = MOVE_TYPE_CUSTOM;
        else moveType = MOVE_TYPE_STANDARD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        label.setText(name);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // Методи життєдіяльності об'єкта

    public void markDeleted() {
        deleted = true;
    }

    public void update(double dt, boolean isPaused) {
        if (!isSelected() && !isPaused) move(dt);
        render();
    }

    public void move(double dt) {
        int speed = 100;
        x += dt * speed * moveVector.x;
        y += dt * speed * moveVector.y;
        invalidatePosition();
    }

    public void render() {
        root.setTranslateX(x);
        root.setTranslateY(y);
        if (moveVector.x > 0)
            imageView.setScaleX(1);
        if (moveVector.x < 0)
            imageView.setScaleX(-1);
        // if (moveVector[0] == 0) -> do nothing
        imageView.setImage(getImage(selected));

        ImageView iv = new ImageView(imageView.getImage());
        iv.setFitWidth(WIDTH);
        iv.setFitHeight(HEIGHT);
        imageView.setClip(iv);

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blush = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        WIDTH,
                        HEIGHT,
                        Color.RED
                )
        );

        imageView.setEffect(moveType == MOVE_TYPE_CUSTOM ? blush : null);
    }

    public void invalidatePosition() {
        if (x < 0) {
            x = 0;
            moveVector.x *= -1;
        }
        if (x > Universal.WIDTH - Pigeon.WIDTH) {
            x = Universal.WIDTH - Pigeon.WIDTH;
            moveVector.x *= -1;
        }
        if (y < 0) {
            y = 0;
            moveVector.y *= -1;
        }
        if (y > Universal.HEIGHT - Pigeon.HEIGHT) {
            y = Universal.HEIGHT - Pigeon.HEIGHT;
            moveVector.y *= -1;
        }
    }

    protected Image getImage(boolean isSelected) {
        return Sprites.getPigeon(isSelected);
    }

    public boolean collideWith(Pigeon other) {
        return getX() < other.getX() + Pigeon.WIDTH &&
                getX() + Pigeon.WIDTH > other.getX() &&
                getY() < other.getY() + Pigeon.HEIGHT &&
                Pigeon.HEIGHT + getY() > other.getY();
    }

    public boolean approachTo(Pigeon other) {
        double x1 = getX(), y1 = getY(), x2 = other.getX(), y2 = other.getY();
        double dx1 = getMoveVector().x, dy1 = getMoveVector().y;
        double dx2 = other.getMoveVector().x, dy2 = other.getMoveVector().y;

        double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double distNext = Math.sqrt(Math.pow(x1 + dx1 - x2 - dx2, 2) + Math.pow(y1 + dy1 - y2 - dy2, 2));

        return dist > distNext;
    }

    // Реалізації інтерфейсів

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public int compareTo(Pigeon o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.name, o.name);
    }

    // Методи суперкласу Object

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name, moveType);
    }

    @Override
    public String toString() {
        return "Pigeon{" +
                "x=" + x +
                ", y=" + y +
                ", moveVector=" + moveVector +
                ", moveType=" + moveType +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Pigeon pigeon = (Pigeon) super.clone();
        pigeon.moveVector = Vector.copyOf(moveVector);
        return pigeon;
    }
}
