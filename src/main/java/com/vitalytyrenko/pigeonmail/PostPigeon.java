package com.vitalytyrenko.pigeonmail;

import javafx.scene.image.Image;

import java.util.Arrays;

public class PostPigeon extends WhitePigeon implements Visualizable {

    private boolean hasMail;

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
    Image getImage(boolean isSelected) {
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
                "x=" + getX() +
                ", y=" + getY() +
                ", moveVector=" + Arrays.toString(getMoveVector()) +
                ", moveType=" + getMoveType() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
