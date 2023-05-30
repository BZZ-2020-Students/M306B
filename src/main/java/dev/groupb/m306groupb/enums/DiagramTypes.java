package dev.groupb.m306groupb.enums;

public enum DiagramTypes {
    USAGE,
    METER;

    public static DiagramTypes fromString(String text) {
        for (DiagramTypes b : DiagramTypes.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
