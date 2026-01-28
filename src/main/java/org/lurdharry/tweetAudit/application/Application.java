package org.lurdharry.tweetAudit.application;

import org.lurdharry.tweetAudit.config.Settings;
import org.lurdharry.tweetAudit.model.Tweet;
import org.lurdharry.tweetAudit.parser.FileType;
import org.lurdharry.tweetAudit.parser.TweetParser;
import org.lurdharry.tweetAudit.service.TweetAnalyzer;
import org.lurdharry.tweetAudit.writer.CSVWriter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class Application {

    private final static Logger logger = Logger.getLogger(Application.class.getName());
    private final Settings settings;
    private final TweetAnalyzer analyzer;


    public Application(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        this.settings = settings;
        this.analyzer = TweetAnalyzer.loadFromSettings(settings);
    }

    /**
    *parses tweet from JSON to CSV format.
    * it transforms  JSON tweet from gotten from twitter into relatable format for processing.
    * */
    public void parseTweets() throws IOException {
        System.out.println("Parsing tweets from " + settings.inputPath() + "...");
        TweetParser parser = new TweetParser(settings.inputPath(), FileType.JSON);
        List<Tweet> tweets;

        try {
            tweets = parser.parse();
        } catch (IOException e) {
            throw new IOException("Failed to parse tweets from " + settings.inputPath()+ e.getMessage());
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



    }


    private List<Tweet> parseTweetFromTransformed () throws IOException {
        TweetParser parser = new TweetParser(settings.outputPath(), FileType.CSV);
        return parser.parse();
    }

    private void writeParseResult(List<Tweet> tweets) throws IOException {
        System.out.println("Writing parse result to " + settings.outputPath() + "...");
        try (CSVWriter writer = CSVWriter.create(settings.outputPath(), false)) {
            writer.writeTweets(tweets);
        } catch (Exception e) {
            throw new IOException("Error writing tweet to csv: " + e.getMessage());
        }
    }

}
