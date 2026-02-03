package org.lurdharry.tweetAudit.config;

import com.google.gson.Gson;
import org.lurdharry.tweetAudit.model.Criteria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

public class ConfigLoader {

    private static final String DEFAULT_CRITERIA_FILE = "criteria.json";
    private final Gson gson;


    public ConfigLoader() {
        this.gson = new Gson();
    }

    public Settings load (){
        Settings.Builder builder = Settings.builder();
        loadFromEnvironment(builder);
        loadCriteria(builder);

        return builder.build();
    }

    private void loadCriteria(Settings.Builder builder) {
        String filePath = getEnv("CRITERIA_PATH").orElse(DEFAULT_CRITERIA_FILE);
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            System.err.println("Warning: Criteria file not found, using defaults");
            return;
        }
        try {
            String json = Files.readString(path);

            Criteria criteria = gson.fromJson(json, Criteria.class);
            if (criteria == null) {
                System.err.println("Warning: Invalid criteria file, using defaults");
                return;
            }
            builder.criteria(criteria);
        } catch (IOException e) {
            System.err.println("Warning: Could not load criteria: " + e.getMessage());
        }
    }

    private void loadFromEnvironment(Settings.Builder builder) {
        getEnv("GEMINI_API_KEY").ifPresent(builder::apiKey);
        getEnv("GEMINI_MODEL").ifPresent(builder::modelName);
        getEnv("TWITTER_USERNAME").ifPresent(builder::username);
        getEnv("TWEETS_ARCHIVE_PATH").ifPresent(builder::inputPath);
        getEnv("PARSED_TWEETS_PATH").ifPresent(builder::extractedPath);
        getEnv("CHECKPOINT_PATH").ifPresent(builder::checkpointPath);
        getEnv("OUTPUT_PATH").ifPresent(builder::outputPath);
        getEnv("BASE_URL").ifPresent(builder::baseUrl);
        getEnv("BATCH_SIZE").ifPresent(value -> builder.batchSize(Integer.parseInt(value)));
        getEnv("RATE_LIMIT_SECONDS").ifPresent(value ->
                builder.rateLimitDelay(Duration.ofMillis((long) (Double.parseDouble(value) * 1000)))
        );
    }

    private static Optional<String> getEnv(String key) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? Optional.of(value) : Optional.empty();
    }
}
