package com.edu.worx.global.utils;

public class MathUtil {

    public static float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }

    public static float interpolate(float x1, float x2, float f) {
        return x1 + (x2 - x1) * f;
    }

    public static float uninterpolate(float x1, float x2, float v) {
        if (x2 - x1 == 0) {
            throw new IllegalArgumentException("Can't reverse interpolate with domain size of 0");
        }
        return (v - x1) / (x2 - x1);
    }

    public static float dist(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static int floorEven(int num) {
        return num & ~0x01;
    }

    public static int roundMult4(int num) {
        return (num + 2) & ~0x03;
    }

    public static boolean isEven(int num) {
        return num % 2 == 0;
    }

    private MathUtil() {
    }
}
