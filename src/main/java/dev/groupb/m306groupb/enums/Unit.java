package dev.groupb.m306groupb.enums;

public enum Unit {
    MIN;

    public static Unit fromString(String unit) {
        switch (unit) {
            case "MIN":
                return MIN;
            default:
                throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }
}
