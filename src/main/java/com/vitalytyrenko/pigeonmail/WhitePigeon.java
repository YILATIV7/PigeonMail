package com.vitalytyrenko.pigeonmail;

import javafx.scene.image.Image;

public class WhitePigeon extends Pigeon implements Visualizable, Cloneable {

    private double timer;
    private boolean attachedToMailBox;
    private MailBox mailBox;
    private Vector lastMoveVector;

    {
        timer = 0;
        attachedToMailBox = false;
    }

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

                lastMoveVector = Vector.normalize(new Vector(x1 - x2, y1 - y2));
                timer -= dt;
            } else {
                lastMoveVector = Vector.copyOf(getMoveVector());
                super.move(dt);
            }
        }
        super.render();
    }

    @Override
    protected Image getImage(boolean isSelected) {
        return Sprites.getWhitePigeon(isSelected);
    }

    public boolean isTimeOut() {
        return timer < 0;
    }

    public boolean isAttachedToMailBox() {
        return attachedToMailBox;
    }

    public void attachToMailBox(MailBox mailBox) {
        if (mailBox.contains(this)) throw new IllegalStateException();
        setMoveVector(Vector.zero());
        mailBox.add(this);
        this.mailBox = mailBox;
        attachedToMailBox = true;
        timer = 5;
    }

    public void detachFromMailBox() {
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
        double dx1 = getMoveVector().x, dy1 = getMoveVector().y;

        double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double distNext = Math.sqrt(Math.pow(x1 + dx1 - x2, 2) + Math.pow(y1 + dy1 - y2, 2));

        return dist > distNext;
    }

    @Override
    public String toString() {
        return "WhitePigeon{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", moveVector=" + getMoveVector() +
                ", moveType=" + getMoveType() +
                ", name='" + getName() + '\'' +
                '}';
    }

    @Override
    public WhitePigeon clone() throws CloneNotSupportedException {
        super.clone();
        return new WhitePigeon(getX(), getY(), Vector.degrees(getMoveVector()), getMoveType(), getName() + "-копія");
    }
}
