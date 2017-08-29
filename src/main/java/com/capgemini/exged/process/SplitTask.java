package com.capgemini.exged.process;

import config.Config;
import org.pmw.tinylog.Logger;
import stats.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.TimerTask;

public class SplitTask extends TimerTask {

    private int total;
    private Instant start;

    public SplitTask() {
        try {
            total = Files.list(Paths.get(Config.getMainConfig().getInputFolder()))
                        .map(path -> {
                            try {
                                return Files.lines(path).count();
                            } catch (IOException e) {
                                Logger.error("Impossible de lire le fichier d'entrée afin d'évaluer le nombre de fichiers fragmentés");
                            }
                            return 0;
                        })
                    .map(nbLines -> nbLines.intValue() / Config.getMainConfig().getTempFilesNumberOfLines())
                    .reduce((nbFilesLeft, nbFilesRight) -> nbFilesLeft+ nbFilesLeft)
                    .orElse(1);
        } catch (IOException e) {
            Logger.error("Impossible de trouver le dossier d'input");
        }
        start = Instant.now();
    }

    @Override
    public void run() {
        Logger.info(String.format("%d/%d ETA: %s", Stats.getNumberSplittedFiles().get(), total, Utils.getETA(Stats.getNumberSplittedFiles().get(), total, start)));
    }
}
