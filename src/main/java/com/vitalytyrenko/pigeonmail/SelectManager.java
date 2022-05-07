package com.vitalytyrenko.pigeonmail;

import java.util.ArrayList;
import java.util.List;

public class SelectManager {

    private final List<Pigeon> pigeons = new ArrayList<>();

    public boolean hasSelected() {
        return pigeons.size() > 0;
    }

    public void add(Pigeon p) {
        pigeons.add(p);
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

    public void move(double[] vector) {
        boolean canBeMoved = true;
        for (Pigeon p : pigeons) {
            if (p.getX() + vector[0] < 0 || p.getX() + vector[0] + Pigeon.WIDTH > Universal.WIDTH
                    || p.getY() + vector[1] < 0 || p.getY() + vector[1] + Pigeon.HEIGHT > Universal.HEIGHT) {
                canBeMoved = false;
                break;
            }
        }

        if (canBeMoved) {
            for (Pigeon p : pigeons) {
                p.setX(p.getX() + vector[0]);
                p.setY(p.getY() + vector[1]);
            }
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
