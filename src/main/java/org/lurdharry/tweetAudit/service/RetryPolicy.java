package org.lurdharry.tweetAudit.service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

public class RetryPolicy {
    private final int maxRetries;
    private final Duration initialDelay;

    private static final Set<String> RETRYABLE_KEYWORDS = Set.of(
            "timeout","connection", "rate limit", "quota", "503", "429", "temporarily unavailable"
    );

    public RetryPolicy(int maxRetries,Duration initialDelay){
        if (maxRetries < 1) {
            throw new IllegalArgumentException("Max retries must be at least 1");
        }
        if (initialDelay == null) {
            throw new IllegalArgumentException("Initial delay must not be null");
        }
        this.maxRetries= maxRetries;
        this.initialDelay = initialDelay;
    }

    public static RetryPolicy withDefaults() {
        return new RetryPolicy(3, Duration.ofSeconds(1));
    }

    public <T> T execute(Callable<T> action) throws Exception {

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
              return   action.call();
            } catch (Exception e) {
                if (!isRetryable(e) || attempt == maxRetries - 1){
                    throw e;
                }
                long delayMs = calculateBackoff(attempt);
                Thread.sleep(delayMs);
            }

        }
        throw new IllegalStateException("Retry loop completed without returning or throwing");
    }

    private long calculateBackoff(int attempt) {
        long exponentialDelay = initialDelay.toMillis() * (1L << attempt);
        long jitter = (long) (Math.random() * 1000);
        return exponentialDelay + jitter;
    }

    private boolean isRetryable (Exception e){
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return  RETRYABLE_KEYWORDS.stream().anyMatch(lower::contains);
    }
}
