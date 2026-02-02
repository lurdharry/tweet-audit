package org.lurdharry.tweetAudit.service;

import java.time.Duration;

public class RateLimiter {
    private final Duration delay;
    private long lastRequestTimeNanos;

    public RateLimiter(Duration delay) {
        if (delay == null) {
            throw new IllegalArgumentException("Min interval cannot be null");
        }
        this.delay = delay;
        lastRequestTimeNanos = 0;
    }

    synchronized void waitIfNeeded() throws InterruptedException {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRequestTimeNanos;
        long requiredNanos = delay.toNanos();

        if (elapsedNanos < requiredNanos){
            long sleepNanos = requiredNanos - elapsedNanos;
            long sleepMillis = (sleepNanos + 999_999) / 1_000_000;
            Thread.sleep(sleepMillis);
            lastRequestTimeNanos = System.nanoTime();
        } else {
            lastRequestTimeNanos = now;
        }

    }
}
