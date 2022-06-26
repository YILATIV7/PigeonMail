package com.vitalytyrenko.pigeonmail;

import javafx.scene.image.Image;

public class PostPigeon extends WhitePigeon implements Visualizable, Cloneable {

    private boolean hasMail;

    public void setCapitan(boolean capitan) {
        super.setCapitan(capitan);
        setName("КАПІТАН 3");
    }

    public PostPigeon() {
        super();
        hasMail = Universal.RANDOMIZER.nextBoolean();
    }

    public PostPigeon(double x, double y, int degrees, int moveType, String name, boolean hasMail) {
        super(x, y, degrees, moveType, name);
        this.hasMail = hasMail;
    }

    @Override
    public void update(double dt, boolean isPaused) {
        super.update(dt, isPaused);
    }

    @Override
    protected Image getImage(boolean isSelected) {
        return Sprites.getPostPigeon(isSelected, hasMail);
    }

    public boolean isHasMail() {
        return hasMail;
    }

    public void setHasMail(boolean hasMail) {
        this.hasMail = hasMail;
    }

    @Override
    public String toString() {
        return "PostPigeon{" +
                "name='" + getName() + '\'' +
                ", x=" + Math.round(getX()) +
                ", y=" + Math.round(getY()) +
                ", moveType=" + getMoveType() +
                ", moveVector=" + getMoveVector() +
                '}';
    }

    @Override
    public PostPigeon clone() throws CloneNotSupportedException {
        super.clone();
        return new PostPigeon(getX(), getY(), Vector.degrees(getMoveVector()), getMoveType(), getName() + "-копія", hasMail);
    }
}
