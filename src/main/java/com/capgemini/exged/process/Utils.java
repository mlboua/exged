package com.capgemini.exged.process;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getETA(int currentValue, int totalValue, Instant start) {
        long eta = currentValue == 0 ? 0 :
                (totalValue - currentValue) * Duration.between(start, Instant.now()).toMillis() / (currentValue);

        return currentValue == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
    }
}
