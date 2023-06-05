package dev.groupb.m306groupb.enums;

public enum ExportTypes {
    CSV,
    JSON;

    public static ExportTypes fromString(String type) {
        for (ExportTypes exportType : ExportTypes.values()) {
            if (exportType.name().equalsIgnoreCase(type)) {
                return exportType;
            }
        }
        // Return JSON as default
        return JSON;
    }
}
