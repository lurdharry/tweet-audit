package org.lurdharry.tweetAudit.service;


import com.google.gson.Gson;
import org.lurdharry.tweetAudit.config.Settings;
import org.lurdharry.tweetAudit.model.*;


public class TweetAnalyzer {

    private final GeminiClient client;
    private final Criteria criteria;
    private final Gson gson;
    private final String baseUrl;
    private final String username;;

    public TweetAnalyzer(GeminiClient client, Criteria criteria, String username, String baseUrl) {
        this.client = client;
        this.criteria = criteria;
        this.baseUrl = baseUrl;
        this.username = username;
        this.gson = new Gson();
    }

    public static TweetAnalyzer loadFromSettings(Settings settings){

        GeminiClient client = new GeminiSdkClient(settings.apiKey(), settings.modelName());

        return new TweetAnalyzer(
                client,
                settings.criteria(),
                settings.username(),
                settings.baseUrl()
        );
    }

    public AnalysisResult analyze(Tweet tweet) throws Exception {
        String prompt = buildPrompt(tweet);

        String response = client.generateContent(prompt);
        if (response == null) {
            throw new Exception("Empty response from Gemini for tweet " + tweet.id());
        }

        GeminiResponse geminiResponse = parseResponse(response, tweet.id());

        Decision decision = Decision.from(geminiResponse.decision());
        String tweetUrl = buildUrl(tweet.id());

        return  new AnalysisResult(tweetUrl, decision, geminiResponse.reason());
    }

    private GeminiResponse parseResponse(String responseJson,String tweetId) throws Exception {

        GeminiResponse response = gson.fromJson(responseJson,GeminiResponse.class);
        if (response == null || response.decision() == null || response.decision().isEmpty() ) {
            throw new Exception("Invalid response for tweet "+tweetId +" missing status detail");
        }
        return response;
    }

    private String buildPrompt(Tweet tweet) {
        StringBuilder rules = new StringBuilder();

        if (!criteria.forbiddenWords().isEmpty()){
            rules.append("- Flag if contains any of these words: ")
                    .append(String.join(", ", criteria.forbiddenWords()))
                    .append("\n");
        }
        if (criteria.professionalCheck()) {
            rules.append("- Flag if unprofessional language or tone\n");
        }
        if (!criteria.tone().isBlank()) {
            rules.append(String.format("- Expected tone: %s%n", criteria.tone()));
        }
        if (criteria.excludePolitics()) {
            rules.append("- Flag if contains political content\n");
        }

        return String.format(
                """
                You are a tweet analyzer. Analyze if this tweet should be flagged for deletion.
                
                Tweet ID: %s
                Tweet: "%s"
                
                Rules:
                    %s
               
                    Respond with JSON only: {"decision": "KEEP" or "DELETE", "reason": "..."}
                """
        ,tweet.id(),tweet.text(),rules);
    }

    private  String buildUrl(String tweetId){
        return  baseUrl + "/" + username + "/status/" + tweetId;
    }

    private static final record GeminiResponse(String decision, String reason) {}
}
