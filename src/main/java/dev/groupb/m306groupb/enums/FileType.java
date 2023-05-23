package dev.groupb.m306groupb.enums;

public enum FileType {
    SDAT,
    ESL;

    public static FileType fromString(String fileType) {
        return switch (fileType.toUpperCase()) {
            case "SDAT" -> SDAT;
            case "ESL" -> ESL;
            default -> throw new IllegalArgumentException("Unknown file type: " + fileType);
        };
    }
}
