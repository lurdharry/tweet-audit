package org.lurdharry.tweetAudit.config;

import org.lurdharry.tweetAudit.model.Criteria;
import org.lurdharry.tweetAudit.parser.FileType;

public record Settings(
          String inputPath,
          FileType fileType,
          String apiKey,
          String modelName,
          String username,
          Criteria criteria,
          String outputPath,
          int batchSize,
          String baseUrl
) {

    public Settings {
        if (inputPath == null || inputPath.isBlank()) {
            throw new IllegalArgumentException("Input path cannot be null or blank");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
    }

    public static Builder builder(){
        return  new Builder();
    }

    public String tweetUrl(String tweetId) {
        return String.format("%s/%s/status/%s", baseUrl, username, tweetId);
    }

    public static final class Builder{
        private String inputPath;
        private FileType fileType = FileType.JSON;
        private String apiKey="";
        private String modelName="gemini-2.5-flash";
        private String username="lurdharry";
        private Criteria criteria = Criteria.defaults();
        private String outputPath = "flagged_tweets.csv";
        private int batchSize = 10;
        private String baseUrl = "https://x.com";

        public Builder inputPath(String inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public Builder fileType(FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder modelName(String modelName){
            this.modelName = modelName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder criteria(Criteria criteria) {
            this.criteria = criteria;
            return this;
        }

        public Builder outputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Settings build() {
            return new  Settings(
                    inputPath,
                    fileType,
                    apiKey,
                    modelName,
                    username,
                    criteria,
                    outputPath,
                    batchSize,
                    baseUrl
            );
        }
    }
}
