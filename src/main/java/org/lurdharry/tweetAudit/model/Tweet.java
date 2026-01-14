package org.lurdharry.tweetAudit.model;

public record Tweet(String id, String text) {

    public Tweet {
        if (id == null || id.isBlank()) {
            throw  new IllegalArgumentException("Tweet ID cannot be null or blank");
        }
        if (text == null) {
            throw  new IllegalArgumentException("Tweet text cannot be null");
        }
    }

    @Override
    public String toString() {
        return "Tweet{" + "id='" + id + '\'' + ", text='" + text + '\'' + "}";
    }

}
