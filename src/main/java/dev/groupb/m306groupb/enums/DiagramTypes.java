package dev.groupb.m306groupb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiagramTypes {
    USAGE(0),
    METER(1);

    private final int priority;

    public static DiagramTypes fromString(String text) {
        for (DiagramTypes b : DiagramTypes.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }


}
