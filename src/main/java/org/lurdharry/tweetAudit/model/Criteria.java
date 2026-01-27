package org.lurdharry.tweetAudit.model;

import java.util.List;

public record Criteria(
        List<String> forbiddenWords,
        boolean professionalCheck,
        String tone,
        boolean excludePolitics
) {

    public static Criteria defaults() {
        return new Criteria(
                List.of("crypto", "NFT", "hustlegrindset"),
                true,
                "respectful and thoughtful",
                true
        );
    }
}
