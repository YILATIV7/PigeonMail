package com.vitalytyrenko.pigeonmail;

public class Vector {

    public static Vector zero() {
        return new Vector(0, 0);
    }

    public static Vector copyOf(Vector v) {
        return new Vector(v.x, v.y);
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
