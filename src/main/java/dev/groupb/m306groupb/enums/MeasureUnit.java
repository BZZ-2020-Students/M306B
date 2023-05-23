package dev.groupb.m306groupb.enums;

public enum MeasureUnit {
    KWH;

    public static MeasureUnit fromString(String unit) {
        if (unit.equals("KWH")) {
            return KWH;
        }
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
}
