package com.capgemini.exged.process;

import org.pmw.tinylog.Logger;
import stats.Stats;

import java.time.Duration;
import java.time.Instant;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TreatmentTask extends TimerTask {

    private Instant start;

    public TreatmentTask() {
        start = Instant.now();
    }

    @Override
    public void run() {
        Logger.info(String.format("%d%% %d/%d, ETA: %s, Row/sec: %d/sec, RAM(MB): %d/%d",
                (int) ((Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()) * 100 / Stats.getNumberDocumentEntry().get()),
                (Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()),
                Stats.getNumberDocumentEntry().get(), Utils.getETA((int) (Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()), (int) (Stats.getNumberDocumentEntry().get()), start),
                (int) Stats.getNumberFileTreatedPerSeconds(), (int) (Runtime.getRuntime().totalMemory() / 1048576.0),
                (int) (Runtime.getRuntime().maxMemory() / 1048576.0)));
    }

    private static String detectDate(String date) {
        return date.substring(0, 4) + "_" + date.substring(4, 6);
    }
}
