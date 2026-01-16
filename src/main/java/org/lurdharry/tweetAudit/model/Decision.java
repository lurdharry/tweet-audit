package org.lurdharry.tweetAudit.model;

public enum Decision {
    KEEP,
    DELETE;

    public static Decision from(String value) {

        return switch (value.trim().toUpperCase()){
            case "KEEP" -> KEEP;
            case "DELETE" -> DELETE;
            default -> throw new IllegalStateException("Unexpected value: " + value.trim().toUpperCase());
        };
    }
}
