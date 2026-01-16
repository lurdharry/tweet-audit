package org.lurdharry.tweetAudit.model;

public record AnalysisResult(String tweetUrl, Decision decision, String reason) {

}
