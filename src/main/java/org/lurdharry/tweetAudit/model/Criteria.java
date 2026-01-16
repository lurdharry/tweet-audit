package org.lurdharry.tweetAudit.model;

import java.util.List;

public record Criteria(
        List<String> forbiddenWords,
        boolean professionalCheck,
        String tone,
        boolean excludePolitics
) {}
