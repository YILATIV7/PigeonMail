package com.vitalytyrenko.pigeonmail;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MailBox implements Iterable<WhitePigeon>, Visualizable {

    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int MAX_CAPACITY;

    static {
        WIDTH = 127;
        HEIGHT = 163;
        MAX_CAPACITY = 20;
    }

    private final String name;
    private final int x, y;
    private final List<WhitePigeon> pigeons;
    private final Node node;
    private final Label label;

    private int mailCount;

    {
        pigeons = new ArrayList<>();
        mailCount = 0;
    }

    public MailBox(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;

        ImageView imageView = new ImageView(Sprites.getMailbox(false));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        label = new Label("0");
        label.setAlignment(Pos.BOTTOM_CENTER);
        label.setFont(Font.font(22));
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 25.0);

        Rectangle rect = new Rectangle(40, 18, Paint.valueOf("#E0BC24"));
        rect.setX(45);
        rect.setY(113);
        rect.setStroke(Paint.valueOf("#92966F"));
        rect.setStrokeWidth(1);

        node = new AnchorPane(imageView, rect, label);
        node.setTranslateX(x);
        node.setTranslateY(y);
    }

    public MailBox(String name) {
        this(name,
                150 + Universal.RANDOMIZER.nextInt(Universal.WIDTH - MailBox.WIDTH - 300),
                150 + Universal.RANDOMIZER.nextInt(Universal.HEIGHT - MailBox.HEIGHT - 300)
        );
    }

    public void update(double dt, boolean isPaused) {
        for (int i = 0; i < pigeons.size(); ) {
            WhitePigeon pigeon = pigeons.get(i);
            pigeon.update(dt, isPaused);
            i++;
        }
        label.setText(String.valueOf(mailCount));
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void add(WhitePigeon pigeon) {
        pigeons.add(pigeon);
    }

    public WhitePigeon get(int index) {
        return pigeons.get(index);
    }

    public void remove(int index) {
        pigeons.remove(index);
    }

    public void remove(WhitePigeon pigeon) {
        pigeons.remove(pigeon);
    }

    public boolean contains(WhitePigeon pigeon) {
        return pigeons.contains(pigeon);
    }

    public int size() {
        return pigeons.size();
    }

    public int getMailCount() {
        return mailCount;
    }

    public void putMail() {
        this.mailCount++;
    }

    public void getMail() {
        this.mailCount--;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        StringBuilder pigeonsStr = new StringBuilder("{\n");

        for (Pigeon p : pigeons) {
            pigeonsStr.append("\t").append(p).append("\n");
        }

        pigeonsStr.append("}");

        return "MailBox{" +
                "name='" + getName() + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", mailCount=" + getMailCount() +
                ", pigeons=" + pigeonsStr +
                '}';
    }

    @Override
    public Iterator<WhitePigeon> iterator() {
        return new Iterator<>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < pigeons.size();
            }

            @Override
            public WhitePigeon next() {
                return pigeons.get(i++);
            }
        };
    }
}
