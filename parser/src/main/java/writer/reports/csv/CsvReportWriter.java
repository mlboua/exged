package writer.reports.csv;

import reports.GenericReportRow;
import stats.Stats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class CsvReportWriter {

    private CsvReportWriter() {
    }

    public static void createCounter(File reportFile, Stats stats) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stats.toString());
        Files.write(reportFile.toPath(), stringBuilder.toString().getBytes());
    }

    public static void createCounter(File reportFile, Stats stats, Map<String, Map<String, List<GenericReportRow>>> errors) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stats.toString())
                .append("\n=================== ERREURS ===================\n");
        errors.forEach((error, detailError) ->
                detailError.forEach((detailErrorName, genericReportRow) -> stringBuilder.append(error).append(": ").append(genericReportRow.size()).append(" - ").append(genericReportRow.get(0).getDetailReject()).append("\n"))
        );
        Files.write(reportFile.toPath(), stringBuilder.toString().getBytes());
    }
}
