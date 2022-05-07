package com.vitalytyrenko.pigeonmail;

import java.util.Arrays;
import java.util.Comparator;

public class Vector implements Comparator<Vector> {

    public static Vector zero() {
        double[] tmp = new double[2];
        Arrays.fill(tmp, 0);
        return new Vector(tmp[0], tmp[1]);
    }

    public static Vector copyOf(Vector v) {
        double[] tmp = { v.x, v.y };
        double[] tmpCopy = Arrays.copyOf(tmp, 2);
        return new Vector(tmpCopy[0], tmpCopy[1]);
    }

    public static Vector normalize(Vector v) {
        double len = Math.sqrt(v.x * v.x + v.y * v.y);
        return new Vector(v.x / len, v.y / len);
    }

    public static int degrees(Vector v) {
        return (int) (Math.atan2(v.y, v.x) * 180 / Math.PI);
    }

    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(int degrees) {
        this(Math.cos(Math.PI * degrees / 180), Math.sin(Math.PI * degrees / 180));
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int compare(Vector o1, Vector o2) {
        return o1.x == o2.x && o1.y == o2.y ? 0
                : (o1.x < o2.x && o1.y < o2.y ? -1 : 1);
    }
}
