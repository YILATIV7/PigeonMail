package com.vitalytyrenko.pigeonmail;

import java.util.ArrayList;
import java.util.List;

public class SelectManager {

    private final List<Pigeon> pigeons = new ArrayList<>();

    public boolean hasSelected() {
        return pigeons.size() > 0;
    }

    public void add(Pigeon p) {
        if (!pigeons.contains(p)) pigeons.add(p);
    }

    public void remove(Pigeon p) {
        pigeons.remove(p);
    }

    public void cancel() {
        pigeons.forEach(p -> p.setSelected(false));
        pigeons.clear();
    }

    public void applyDelete() {
        pigeons.forEach(Pigeon::markDeleted);
        pigeons.clear();
    }

    public void applySwapMoveType() {
        pigeons.forEach(Pigeon::swapMoveType);
    }

    public void applyClone() {
        try {
            for (Pigeon p : pigeons) {
                Universal.getInstance().bindPigeonToScene(p.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void move(Vector vector) {
        boolean isRightMoving = vector.x > 0,
                isBottomMoving = vector.y > 0;
        Vector maxVector = Vector.copyOf(vector);

        for (Pigeon p : pigeons) {
            double pigeonX = p.getX(),
                    pigeonY = p.getY();

            double x;
            if (isRightMoving) {
                x = Universal.WIDTH - Pigeon.WIDTH - pigeonX;
                if (x < maxVector.x) maxVector.x = x;
            } else {
                x = -pigeonX;
                if (x > maxVector.x) maxVector.x = x;
            }

            double y;
            if (isBottomMoving) {
                y = Universal.HEIGHT - Pigeon.HEIGHT - pigeonY;
                if (y < maxVector.y) maxVector.y = y;
            } else {
                y = -pigeonY;
                if (y > maxVector.y) maxVector.y = y;
            }
        }

        for (Pigeon p : pigeons) {
            p.setX(p.getX() + maxVector.x);
            p.setY(p.getY() + maxVector.y);
        }
    }

    public double getCenterX() {
        double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (Pigeon p : pigeons) {
            if (p.getX() < min) min = p.getX();
            if (p.getX() > max) max = p.getX();
        }
        return (min + max + Pigeon.WIDTH) / 2;
    }

    public double getCenterY() {
        double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (Pigeon p : pigeons) {
            if (p.getY() < min) min = p.getY();
            if (p.getY() > max) max = p.getY();
        }
        return (min + max + Pigeon.HEIGHT) / 2;
    }
}
