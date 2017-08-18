package report;

import cyclops.async.adapters.Topic;
import data.Fold;
import org.pmw.tinylog.Logger;
import reader.config.mapping.reports.ReportsConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;

public class GenericReport {

    private GenericReport() {
    }

    public static void createReport(String pathReportDirectory, List<ReportsConfig> reportsConfigList, Topic<Fold> foldTopic) {
        reportsConfigList.forEach(reportsConfig -> {
            try (OutputStreamWriter outputStreamWriter =
                         new OutputStreamWriter(new FileOutputStream(new File(pathReportDirectory + File.separator + reportsConfig.getName() + ".csv")))) {
                if (reportsConfig.getHeaders().get(0).equalsIgnoreCase("all")) {
                    writeString(outputStreamWriter, setToCsvString(foldTopic.stream().limit(1).findFirst().get().getHeader().keySet()));
                    foldTopic.stream()
                            .filter(fold -> fold.getStatus() == reportsConfig.getFoldStatus() || "all".equalsIgnoreCase(reportsConfig.getFoldStatus().name()))
                            .flatMap(fold -> fold.getHeader().keySet().stream().map(header -> fold.getValue(0, header)))
                            .grouped(250)
                            .forEach(csvStringList -> writeString(outputStreamWriter, csvStringList.join("\n")));
                } else {
                    writeString(outputStreamWriter, GenericReport.listToCsvString(reportsConfig.getHeaders()));
                    foldTopic.stream()
                            .filter(fold -> fold.getStatus() == reportsConfig.getFoldStatus() || "all".equalsIgnoreCase(reportsConfig.getFoldStatus().name()))
                            .flatMap(fold -> reportsConfig.getHeaders().stream().map(header -> fold.getValue(0, header)))
                            .grouped(250)
                            .forEach(csvStringList -> writeString(outputStreamWriter, csvStringList.join("\n")));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        });
    }

    private static void writeString(OutputStreamWriter outputStreamWriter, String stringToWrite) {
        try {
            outputStreamWriter.write(stringToWrite);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private static String listToCsvString(List<String> stringList) {
        return stringList.stream().reduce((leftStr, rigthStr) -> leftStr + "," + rigthStr).orElseGet(null);
    }

    private static String setToCsvString(Set<String> stringSet) {
        return stringSet.stream().reduce((leftStr, rigthStr) -> leftStr + "," + rigthStr).orElseGet(null);
    }
}
