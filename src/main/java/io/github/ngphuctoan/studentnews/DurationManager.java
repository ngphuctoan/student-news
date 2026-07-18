package io.github.ngphuctoan.studentnews;

public class DurationManager {
    private long startTimer;

    public DurationManager() {
        resetTimer();
    }

    public void resetTimer() {
        startTimer = System.nanoTime();
    }

    static long convertToMilliSeconds(long nanoSeconds) {
        return nanoSeconds / 1_000_000;
    }

    public long getDurationInMilliSeconds() {
        long nowTimer = System.nanoTime();
        return convertToMilliSeconds(nowTimer - startTimer);
    }
}
