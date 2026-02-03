package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class MathUtils {
    private MathUtils() {
    }

    public static int signum(double num) {
        if (num < 0.0d) {
            return -1;
        }
        if (num == 0.0d) {
            return 0;
        }
        return 1;
    }

    public static double lerp(double start, double stop, double amount) {
        return ((1.0d - amount) * start) + (amount * stop);
    }

    public static int clampInt(int min, int max, int input) {
        if (input < min) {
            return min;
        }
        if (input > max) {
            return max;
        }
        return input;
    }

    public static double clampDouble(double min, double max, double input) {
        if (input < min) {
            return min;
        }
        if (input > max) {
            return max;
        }
        return input;
    }

    public static int sanitizeDegreesInt(int degrees) {
        int degrees2 = degrees % 360;
        if (degrees2 < 0) {
            return degrees2 + 360;
        }
        return degrees2;
    }

    public static double sanitizeDegreesDouble(double degrees) {
        double degrees2 = degrees % 360.0d;
        if (degrees2 < 0.0d) {
            return degrees2 + 360.0d;
        }
        return degrees2;
    }

    public static double rotationDirection(double from, double to) {
        double increasingDifference = sanitizeDegreesDouble(to - from);
        return increasingDifference <= 180.0d ? 1.0d : -1.0d;
    }

    public static double differenceDegrees(double a, double b) {
        return 180.0d - Math.abs(Math.abs(a - b) - 180.0d);
    }

    public static double[] matrixMultiply(double[] row, double[][] matrix) {
        double a = (row[0] * matrix[0][0]) + (row[1] * matrix[0][1]) + (row[2] * matrix[0][2]);
        double b = (row[0] * matrix[1][0]) + (row[1] * matrix[1][1]) + (row[2] * matrix[1][2]);
        double c = (row[0] * matrix[2][0]) + (row[1] * matrix[2][1]) + (row[2] * matrix[2][2]);
        return new double[]{a, b, c};
    }
}
