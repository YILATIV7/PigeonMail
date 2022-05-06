package com.vitalytyrenko.pigeonmail;

import javafx.scene.image.Image;

import java.util.Arrays;

public class WhitePigeon extends Pigeon implements Visualizable {

    private double timer = 0;
    private boolean attachedToMailBox = false;
    private MailBox mailBox;

    private double[] lastMoveVector;

    public WhitePigeon() {
        super();
    }

    public WhitePigeon(double x, double y, int degrees, int moveType, String name) {
        super(x, y, degrees, moveType, name);
    }

    @Override
    public void update(double dt, boolean isPaused) {
        if (!isSelected() && !isPaused) {
            if (attachedToMailBox) {
                double x1 = getX() + Pigeon.WIDTH / 2.0,
                        y1 = getY() + Pigeon.HEIGHT / 2.0,
                        x2 = mailBox.getX() + MailBox.WIDTH / 2.0,
                        y2 = mailBox.getY() + MailBox.HEIGHT / 2.0;

                lastMoveVector = normalize(new double[]{x1 - x2, y1 - y2});
                timer -= dt;
            } else {
                lastMoveVector = new double[]{getMoveVector()[0], getMoveVector()[1]};
                super.move(dt);
            }
        }
        super.render();
    }

    @Override
    Image getImage(boolean isSelected) {
        return Sprites.getWhitePigeon(isSelected);
    }

    public boolean isTimeOut() {
        return timer < 0;
    }

    public boolean isAttachedToMailBox() {
        return attachedToMailBox;
    }

    void attachToMailBox(MailBox mailBox) {
        if (mailBox.contains(this)) throw new IllegalStateException();
        setMoveVector(new double[]{0, 0});
        mailBox.add(this);
        this.mailBox = mailBox;
        attachedToMailBox = true;
        timer = 5;
    }

    void detachFromMailBox() {
        if (mailBox == null) throw new IllegalStateException();
        setMoveVector(lastMoveVector);
        mailBox.remove(this);
        mailBox = null;
        attachedToMailBox = false;
        timer = 0;
    }

    public boolean nearTo(MailBox mailBox) {
        double centerX = getX() + WIDTH / 2.0,
                centerY = getY() + HEIGHT / 2.0;
        return centerX > mailBox.getX() && centerX < mailBox.getX() + MailBox.WIDTH
                && centerY > mailBox.getY() && centerY < mailBox.getY() + MailBox.HEIGHT;
    }

    public boolean approachTo(MailBox mailBox) {
        double x1 = getX() + Pigeon.WIDTH / 2.0,
                y1 = getY() + Pigeon.HEIGHT / 2.0,
                x2 = mailBox.getX() + MailBox.WIDTH / 2.0,
                y2 = mailBox.getY() + MailBox.HEIGHT / 2.0;
        double dx1 = getMoveVector()[0], dy1 = getMoveVector()[1];

        double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double distNext = Math.sqrt(Math.pow(x1 + dx1 - x2, 2) + Math.pow(y1 + dy1 - y2, 2));

        return dist > distNext;
    }

    @Override
    public String toString() {
        return "WhitePigeon{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", moveVector=" + Arrays.toString(getMoveVector()) +
                ", moveType=" + getMoveType() +
                ", name='" + getName() + '\'' +
                '}';
    }

    private static double[] normalize(double[] v) {
        double len = Math.sqrt(v[0] * v[0] + v[1] * v[1]);

        return new double[]{
                v[0] / len,
                v[1] / len
        };
    }
}
