package report;

import config.mapping.reports.ReportsConfig;
import cyclops.async.LazyReact;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CyclopsCollectors;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import data.Fold;
import data.FoldStatus;
import org.jooq.lambda.tuple.Tuple2;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenericReport {

    private static final Map<String, Path> fileMap = new HashMap<>();
    private static final Map<String, ListX<String>> headerMap = new HashMap<>();

    private GenericReport() {
    }

    private static void createReportString(String pathReportDirectory, ReportsConfig reportsConfig, List<Fold> foldList) {
        ListX<Fold> foldListXReport = FoldStatus.ALL.compareTo(reportsConfig.getFoldStatus()) == 0 ?
                foldList.stream().collect(CyclopsCollectors.toListX())
                : foldList.stream().filter(fold -> fold.getStatus().compareTo(reportsConfig.getFoldStatus()) == 0).collect(CyclopsCollectors.toListX());
        if(foldListXReport.isEmpty())
            return;
        if(reportsConfig.getFoldStatus().compareTo(FoldStatus.REJECT) == 0)
            System.out.println(foldListXReport.size());

        if (!fileMap.containsKey(reportsConfig.getName())) {
            try {
                fileMap.put(reportsConfig.getName(), Paths.get(pathReportDirectory + File.separator + reportsConfig.getName() + ".csv"));

                Logger.info("Création du fichier de rapport \""+reportsConfig.getName()+ "\"");
                headerMap.put(reportsConfig.getName(),
                        "all".equalsIgnoreCase(reportsConfig.getHeaders().get(0)) ?
                                ReactiveSeq.fromStream(foldListXReport.get(0).getHeader().keySet().stream()).toListX()
                                : ReactiveSeq.fromStream(reportsConfig.getHeaders().stream()).toListX());

                Files.write(fileMap.get(reportsConfig.getName()),
                        headerMap.get(reportsConfig.getName()).join(",").getBytes());
            } catch (IOException e) {
                Logger.error("Impossible de supprimer l'anciens rapport ou créer un nouveau fichier: " + pathReportDirectory + File.separator + reportsConfig.getName() + ".csv, Erreur: " + e);
            }
        }
        try {
            Files.write(fileMap.get(reportsConfig.getName()),
                    ReactiveSeq.fromStream(foldListXReport.stream().flatMap(fold ->
                            fold.getData().stream()
                                .map(row -> headerMap.get(reportsConfig.getName())
                                        .map(header -> row.get(fold.getHeader().get(header))).stream().join(",")
                    )))
                            .join("\n")
                            .getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.error("Impossible d'ajouter les plis au fichier de rapport: " + e);
        }
    }

    public static void createReport(String pathReportDirectory, int groupSize, List<ReportsConfig> reportsConfigList, Stream<Fold> foldStream) {
        FutureStream.builder().fromStream(foldStream)
        .grouped(groupSize)
                .forEach(foldList -> reportsConfigList.forEach(reportsConfig -> createReportString(pathReportDirectory, reportsConfig, foldList)));
    }
}
