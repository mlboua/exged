package writer.reports;

import stats.Stats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReportWriter {

    private ReportWriter() {
    }

    public static void createCounter(File reportFile, Stats stats) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stats.toString());
        Files.write(reportFile.toPath(), stringBuilder.toString().getBytes());
    }
}
