package org.lurdharry.tweetAudit;


import org.lurdharry.tweetAudit.application.Application;
import org.lurdharry.tweetAudit.config.Settings;

import java.io.IOException;

public class Main {

    private static final String EXTRACT = "extract";
    private static final String ANALYZE = "analyze";

    public static void main(String[] args) {

        if (args.length  <1) {
            printUsageInstruction();
            System.exit(1);
        }

        String command = args[0];

        try {
            Settings settings = buildSettings();
            Application application = new Application(settings);
            switch (command){
                case EXTRACT -> application.parseTweets();
                case ANALYZE -> application.analyzeTweets();
                default -> {
                    System.err.println("Unknown command: "+ command);
                    printUsageInstruction();
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }


    }

    private static Settings buildSettings() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null|| apiKey.isBlank()) {
            System.err.println("Error: GEMINI_API_KEY environment variable not set");
            System.exit(1);
        }
        return Settings.builder()
                .apiKey(apiKey)
                .build();
    }

    private static void printUsageInstruction() {
        System.out.println("""
                Usage: java main command
                
                Commands:
                    extract    Parse JSON archive and save as CSV (run once)
                    analyze    Analyze tweets in batches (run multiple times)
                
                Environment Variables:
                          GEMINI_API_KEY    Your Gemini API key (required)
               
                        Examples:
                          java Main extract
                          java Main analyze
                """
        );
    }
}