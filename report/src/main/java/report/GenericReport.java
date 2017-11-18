package report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import config.mapping.reports.ReportsConfig;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CyclopsCollectors;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import data.Fold;
import data.FoldStatus;

public class GenericReport {

    private static final Map<String, Path>          fileMap   = new HashMap<>();
    private static final Map<String, ListX<String>> headerMap = new HashMap<>();

    private GenericReport() {
    }

    private static void createReportString(final String pathReportDirectory, final ReportsConfig reportsConfig,
            final List<Fold> foldList) {
        final ListX<Fold> foldListXReport = FoldStatus.ALL.compareTo(reportsConfig.getFoldStatus()) == 0
                ? foldList.stream().collect(CyclopsCollectors.toListX())
                : foldList.stream().filter(fold -> fold.getStatus().compareTo(reportsConfig.getFoldStatus()) == 0)
                        .collect(CyclopsCollectors.toListX());
        if (foldListXReport.isEmpty()) {
            return;
        }

        if (!fileMap.containsKey(reportsConfig.getName())) {
            try {
                fileMap.put(reportsConfig.getName(),
                        Paths.get(pathReportDirectory + File.separator + reportsConfig.getName() + ".csv"));

                Logger.info("Création du fichier de rapport \"" + reportsConfig.getName() + "\"");
                headerMap.put(reportsConfig.getName(),
                        "all".equalsIgnoreCase(reportsConfig.getHeaders().get(0))
                                ? ReactiveSeq.fromStream(foldListXReport.get(0).getHeader().keySet().stream()).toListX()
                                : ReactiveSeq.fromStream(reportsConfig.getHeaders().stream()).toListX());

                Files.write(fileMap.get(reportsConfig.getName()),
                        headerMap.get(reportsConfig.getName()).join(",").concat("\n").getBytes());
            } catch (final IOException e) {
                Logger.error("Impossible de supprimer l'anciens rapport ou créer un nouveau fichier: "
                        + pathReportDirectory + File.separator + reportsConfig.getName() + ".csv, Erreur: " + e);
            }
        }
        try {
            Files.write(fileMap.get(reportsConfig.getName()), ReactiveSeq
                    .fromStream(foldListXReport.stream()
                            .flatMap(fold -> fold.getData().stream()
                                    .map(row -> headerMap.get(reportsConfig.getName())
                                            .map(header -> row.get(fold.getHeader().get(header))).stream().join(","))))
                    .join("\n").getBytes(), StandardOpenOption.APPEND);
        } catch (final IOException e) {
            Logger.error("Impossible d'ajouter les plis au fichier de rapport: " + e);
        }
    }

    public static void createReport(final String pathReportDirectory, final int groupSize,
            final List<ReportsConfig> reportsConfigList, final Stream<Fold> foldStream) {
        FutureStream.builder().fromStream(foldStream).grouped(groupSize).forEach(foldList -> reportsConfigList
                .forEach(reportsConfig -> createReportString(pathReportDirectory, reportsConfig, foldList)));
    }
}
