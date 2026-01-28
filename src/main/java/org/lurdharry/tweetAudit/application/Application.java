package org.lurdharry.tweetAudit.application;

import org.lurdharry.tweetAudit.config.Settings;
import org.lurdharry.tweetAudit.model.AnalysisResult;
import org.lurdharry.tweetAudit.model.Decision;
import org.lurdharry.tweetAudit.model.Tweet;
import org.lurdharry.tweetAudit.parser.FileType;
import org.lurdharry.tweetAudit.parser.TweetParser;
import org.lurdharry.tweetAudit.service.TweetAnalyzer;
import org.lurdharry.tweetAudit.storage.Checkpoint;
import org.lurdharry.tweetAudit.writer.CSVWriter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class Application {

    private final static Logger logger = Logger.getLogger(Application.class.getName());
    private final Settings settings;
    private final TweetAnalyzer analyzer;
    private final Checkpoint checkpoint;


    public Application(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        this.settings = settings;
        this.analyzer = TweetAnalyzer.loadFromSettings(settings);
        this.checkpoint = new Checkpoint(settings.checkpointPath());
    }

    /**
    *parses tweet from JSON to CSV format.
    * it transforms  JSON tweet from gotten from tweeter into relatable format for processing.
    * */
    public void parseTweets() throws IOException {
        logger.info("Parsing tweets from " + settings.inputPath());
        TweetParser parser = new TweetParser(settings.inputPath(), FileType.JSON);
        List<Tweet> tweets;

        try {
            tweets = parser.parse();
        } catch (IOException e) {
            String msg = "Failed to parse tweets from " + settings.inputPath() + " " + e.getMessage();
            logger.severe(msg);
            throw new IOException(msg);
        }
        writeParseResult(tweets);
    }


    public void analyzeTweets() throws IOException {

        List<Tweet> tweets;
        try {
            TweetParser parser = new TweetParser(settings.outputPath(), FileType.CSV);
            tweets = parser.parse();
        } catch (IOException e) {
            throw new IOException("Unable to parse tweet to "+ FileType.CSV.toString());
        }

        if (tweets == null || tweets.isEmpty()) {
            logger.warning("Parsed tweet file is empty, No tweet is analyzed");
            return;
        }

        // check checkpoint to see the progress of any prev processing
        int startIndex;
        startIndex = checkpoint.load();

        if (startIndex >= tweets.size()){
            logger.info("All " + tweets.size() + " tweets has been analyzed");
            return;
        }

        int endIndex = Math.min(startIndex + settings.batchSize(), tweets.size());
        logger.info(String.format("Processing batch %s -%s of %s", startIndex+1, endIndex, tweets.size()));

        int analyzed = 0;
        int flagged = 0;

        try (CSVWriter writer = CSVWriter.create(settings.outputPath(), true)) {
            for (int i = startIndex; i < endIndex; i++) {
                Tweet tweet = tweets.get(i);

                try {
                    AnalysisResult  result = analyzer.analyze(tweet);
                    analyzed++;

                    if (result.decision() == Decision.DELETE){
                        flagged++;
                        writer.writeResult(result);
                        logger.info("Flagged: " + tweet.id());
                    }
                } catch (Exception e) {
                    logger.severe("Error analyzing tweet " + tweet.id() + ": " + e.getMessage());
                    throw new IOException("Failed to analyze tweet " + tweet.id(), e);
                }
            }

        } catch (Exception e) {
            logger.severe("Failed to write results: " + e.getMessage());
            throw new IOException("Failed to write results: " + e.getMessage(), e);
        }

        try {
            checkpoint.save(endIndex);
            logger.info("Last processed index" + endIndex);
        } catch (IOException e) {
            logger.severe("Error saving last index: " + e.getMessage());
            throw new IOException("Error saving last index: " + e.getMessage(), e);
        }
        logger.info(String.format("Batch processing complete! Analyzed %d, flagged %d (%d/%d total)%n",analyzed, flagged, endIndex, tweets.size()));
    }


    private List<Tweet> parseTweetFromTransformed () throws IOException {
        TweetParser parser = new TweetParser(settings.outputPath(), FileType.CSV);
        return parser.parse();
    }

    private void writeParseResult(List<Tweet> tweets) throws IOException {
        System.out.println("Writing parse result to " + settings.extractedPath());
        try (CSVWriter writer = CSVWriter.create(settings.extractedPath(), false)) {
            writer.writeTweets(tweets);
            logger.info("Success writing parsed tweet");
        } catch (Exception e) {
            logger.severe("Error writing tweets");
            throw new IOException("Error writing tweet to csv: " + e.getMessage());
        }
    }

}
