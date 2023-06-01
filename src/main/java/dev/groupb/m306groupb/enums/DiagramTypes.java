package dev.groupb.m306groupb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@AllArgsConstructor
public enum DiagramTypes {
    USAGE(0, "Verbrauchsdiagramm"),
    METER(1, "Zählerstanddiagramm");

    private final int priority;
    private final String german;

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

    public static String toJson() {
        List<DiagramTypes> diagramTypesList = Arrays.asList(DiagramTypes.values());
        diagramTypesList.sort(Comparator.comparingInt(DiagramTypes::getPriority));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (DiagramTypes diagramType : diagramTypesList) {
            stringBuilder.append("{");
            stringBuilder.append("\"priority\":").append(diagramType.getPriority()).append(",");
            stringBuilder.append("\"name\":\"").append(diagramType.getGerman()).append("\",");
            stringBuilder.append("\"value\":\"").append(diagramType).append("\"");
            stringBuilder.append("},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static String toJson(DiagramTypes diagramType) {
        return "{" +
                "\"priority\":" + diagramType.getPriority() + "," +
                "\"name\":\"" + diagramType.getGerman() + "\"," +
                "\"value\":\"" + diagramType + "\"" +
                "}";
    }
}
