package dev.groupb.m306groupb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@AllArgsConstructor
public enum DiagramTypes {
    USAGE(0),
    METER(1);

    private final int priority;

    public static DiagramTypes fromString(String text) {
        if (text != null && !text.isBlank()) {
            for (DiagramTypes b : DiagramTypes.values()) {
                if (b.toString().equalsIgnoreCase(text)) {
                    return b;
                }
            }
        }

        // Return default value if no match (default value is the one with the lowest priority)
        List<DiagramTypes> diagramTypesList = Arrays.asList(DiagramTypes.values());
        diagramTypesList.sort(Comparator.comparingInt(DiagramTypes::getPriority));
        return diagramTypesList.get(0);
    }
}
