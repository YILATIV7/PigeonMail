package com.vitalytyrenko.pigeonmail;

import java.util.Arrays;

public class Vector {

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
}
