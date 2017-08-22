package report;

import config.mapping.reports.ReportsConfig;
import cyclops.stream.ReactiveSeq;
import data.Fold;
import org.jooq.lambda.tuple.Tuple2;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class GenericReport {

    private GenericReport() {
    }

    public static void createReport(String pathReportDirectory, List<ReportsConfig> reportsConfigList, Stream<Fold> foldTopic) {
        reportsConfigList.forEach(reportsConfig -> {
            try (OutputStreamWriter outputStreamWriter =
                         new OutputStreamWriter(new FileOutputStream(new File(pathReportDirectory + File.separator + reportsConfig.getName() + ".csv")))) {
                if (reportsConfig.getHeaders().get(0).equalsIgnoreCase("all")) {
                    Tuple2<ReactiveSeq<Fold>, ReactiveSeq<Fold>> foldStreamDuplicated = ReactiveSeq.fromStream(foldTopic).duplicate();
                    writeString(outputStreamWriter, setToCsvString(foldStreamDuplicated.v1.firstValue().getHeader().keySet()));
                    foldStreamDuplicated.v2
                            .filter(fold -> fold.getStatus() == reportsConfig.getFoldStatus() || "all".equalsIgnoreCase(reportsConfig.getFoldStatus().name()))
                            .flatMap(fold -> fold.getHeader().keySet().stream().map(header -> {

                                System.out.println(fold);
                                return fold.getValue(0, header);
                            }))
                            .grouped(250)
                            .forEach(csvStringList -> writeString(outputStreamWriter, csvStringList.join("\n")));
                } else {
                    writeString(outputStreamWriter, GenericReport.listToCsvString(reportsConfig.getHeaders()));
                    ReactiveSeq.fromStream(foldTopic.filter(fold -> fold.getStatus() == reportsConfig.getFoldStatus() || "all".equalsIgnoreCase(reportsConfig.getFoldStatus().name()))
                            .flatMap(fold -> reportsConfig.getHeaders().stream().map(header -> fold.getValue(0, header))))
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
