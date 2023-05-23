package dev.groupb.m306groupb.enums;

public enum Unit {
    MIN;

    public static Unit fromString(String unit) {
        if (unit.equals("MIN")) {
            return MIN;
        }
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
}
