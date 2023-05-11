package dev.groupb.m306groupb.enums;

public enum MeasureUnit {
    KWH;

    public static MeasureUnit fromString(String unit) {
        switch (unit) {
            case "KWH":
                return KWH;
            default:
                throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }
}
