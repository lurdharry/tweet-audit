package org.lurdharry.tweetAudit.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

public class GeminiSdkClient implements GeminiClient {

    private final Client client;
    private final String modelName;
    private final GenerateContentConfig config;

    public GeminiSdkClient(String apiKey,String modelName) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("modelName cannot be null or blank");
        }
        this.client = Client.builder().apiKey(apiKey).build();
        this.modelName= modelName;
        this.config = GenerateContentConfig.builder().responseMimeType("application/json").build();
    }

    @Override
    public String generateContent(String prompt) throws Exception {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        GenerateContentResponse response = this.client.models.generateContent(modelName,prompt,config);

        if (response.text() == null|| response.text().isBlank()) {
            throw new Exception("No response from gemini");
        }

        return response.text();
    }
}
