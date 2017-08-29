package com.capgemini.exged;

import com.capgemini.exged.exception.ExgedMainException;
import com.capgemini.exged.initialiser.ConfigInitializer;
import com.capgemini.exged.process.SplitTask;
import com.capgemini.exged.process.TreatmentTask;
import com.google.common.io.Files;
import config.Config;
import creator.GenericCreator;
import cyclops.async.LazyReact;
import cyclops.async.QueueFactories;
import cyclops.async.adapters.Queue;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import data.Fold;
import data.FoldStatus;
import engine.TemplateEngineExecutor;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.pmw.tinylog.Logger;
import reader.csv.CsvReader;
import reader.csv.StreamCsvParser;
import report.GenericReport;
import reports.GenericReportRow;
import stats.Stats;
import validator.DetailReject;
import validator.GenericValidator;
import writer.xml.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class App {

    public static void main(String... args) {
        try {
            Logger.info("Initialisation");
            Stats.init();
            // Chargement en mémoire des fichiers de configuration json/yaml
            ConfigInitializer.initConfig(args);
            // Préparation du lecteur de fichier CSV
            final CsvReader reader = new CsvReader(true,
                    Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));
            // Moteur de template
            final TemplateEngineExecutor templateEngineExecutor = new TemplateEngineExecutor(Config.getMainConfig());

            final GenericValidator genericValidator = new GenericValidator(Config.getValidators(), Config.getRejects());
            final GenericCreator genericCreator = new GenericCreator(Config.getCreators());

            // Ajout des stats d'entrée
            Stats.addNumberDocumentEntry(reader.countRowsFolder(new File(Config.getMainConfig().getInputFolder())).intValue());
            Stats.addNumberFilesEntry((int) java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder())).count());


            //final QueueX<GenericReportRow> rejectQueueX = QueueX.<GenericReportRow>empty().lazy();
            final Queue<GenericReportRow> rejectQueueX = QueueFactories.<GenericReportRow>unboundedQueue().build();
            Logger.info("Fragmentation des fichiers");

            final Timer spitTimer = new Timer(true);
            spitTimer.scheduleAtFixedRate(new SplitTask(), 2000, 3000);
            java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder()))
                    .forEach(path -> {
                        try {
                            reader.splitFile(path.toFile(),
                                    new File(Config.getMainConfig().getSplittedTempFolder()),
                                    Config.getMainConfig().getTempFilesNumberOfLines(),
                                    Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()),
                                    Stats.getNumberSplittedFiles());
                        } catch (ExgedParserException e) {
                            e.printStackTrace();
                        }
                    });
            spitTimer.cancel();
            // Lancement des stats dans la console
            final Timer treatmentTimer = new Timer(true);
            treatmentTimer.scheduleAtFixedRate(new TreatmentTask(), 2000, 3000);

            Logger.info("Lancement du traitement");

            Boolean isProd = Config.getMainConfig().getMode().equalsIgnoreCase("prod");

            StreamCsvParser streamCsvParser = new StreamCsvParser(Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));
            try (FutureStream<Fold> folds = new LazyReact(100, 100).fromStream(streamCsvParser.readFolderParallel(new File(Config.getMainConfig().getSplittedTempFolder())))) {
                ReactiveSeq<Fold> concatStream = folds.map(fold -> new Tuple2<>(fold, genericValidator.validateFold(fold)))
                        .peek((Tuple2<Fold, Optional<List<DetailReject>>> tupleFoldReject) -> {
                                    Stats.addNumberPliEntry(1);
                                    tupleFoldReject.v2.ifPresent(rejectList -> {
                                        rejectList.forEach(reject -> {
                                            if (reject.getValues().isPresent()) {
                                                rejectQueueX.add(new GenericReportRow(reject.getCode(), reject.getDetail() + ": " + reject.getValues().get(), tupleFoldReject.v1));
                                            } else {
                                                rejectQueueX.add(new GenericReportRow(reject.getCode(), reject.getDetail(), tupleFoldReject.v1));
                                            }
                                        });
                                    });
                                }
                        )
                        .filter(tupleFoldReject -> !tupleFoldReject.v2.isPresent())
                        .map(Tuple2::v1)
                        .peek(genericCreator::createFields)
                        .peek(fold -> fold.setId(fold.getValue(0, "ID_PLI")))
                        .async()
                        .map(fold -> {
                            Map<String, Object> params = new HashMap<>();
                            params.put("pli", fold.getData());
                            params.put("headers", fold.getHeader());
                            return new Tuple2<>(fold, templateEngineExecutor.render(params));
                        })
                        .sync()
                        .filter(tupleFoldRender -> {
                            if (tupleFoldRender.v2.isPresent()) {
                                return true;
                            } else {
                                rejectQueueX.add(new GenericReportRow("Render-1", "Erreur lors de l'appel du générateur de template", tupleFoldRender.v1));
                                return false;
                            }
                        })
                        .map(tupleFoldRender -> new Tuple3<>(tupleFoldRender.v1,
                                tupleFoldRender.v2,
                                new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                        + detectDate(tupleFoldRender.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                        + tupleFoldRender.v1.getId() + ".xml")))
                        .async()
                        .peek(tupleFoldRenderFile -> {
                            try {
                                java.nio.file.Files.createDirectories(Paths.get(Config.getMainConfig().getOutputTempFolder() + File.separator
                                        + detectDate(tupleFoldRenderFile.v1.getValue(0, "DB_DATE_NUM"))));
                            } catch (IOException e) {
                                Logger.error("Impossible de créer les dossiers parent au fichier d'output: " + e);
                            }
                        })
                        .peek(tupleFoldRenderFile -> {
                            try {
                                java.nio.file.Files.write(tupleFoldRenderFile.v3.toPath(), XMLUtils.toPrettyString(tupleFoldRenderFile.v2.orElseGet(null), 2).getBytes());
                            } catch (IOException e) {
                                Logger.error("Impossible d'écrire sur le système: " + e);
                            }
                        })
                        .peek(tupleFoldRenderFile -> {
                            try {
                                if (isProd) {
                                    Files.copy(new File(Config.getMainConfig().getExternFilesPath() + File.separator +
                                                    tupleFoldRenderFile.v1.getValue(0, "FLENAMED")),
                                            new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                                    + detectDate(tupleFoldRenderFile.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                                    + tupleFoldRenderFile.v1.getId() + ".tif"));
                                }
                                tupleFoldRenderFile.v1.setStatus(FoldStatus.TREATED);
                                Stats.addNumberDocumentExit(tupleFoldRenderFile.v1.getData().size());
                                Stats.addNumberPliExit(1);
                                Stats.addNumberFileExit(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .map(Tuple3::v1)
                        .concat(rejectQueueX.stream()
                                .peek(genericReportRow -> genericReportRow.getFold().setStatus(FoldStatus.REJECT))
                                .peek(genericReportRow -> {
                                    genericReportRow.getFold().getData().forEach(row -> row.add(genericReportRow.getRejectCode() + " - " + genericReportRow.getDetailReject()));
                                    genericReportRow.getFold().getHeader().put("EXGED_ERRORS", genericReportRow.getFold().getData().get(0).size() - 1);
                                    if (Stats.getRejectCounter().containsKey(genericReportRow.getRejectCode() + " - " + genericReportRow.getDetailReject())) {
                                        Stats.getRejectCounter().get(genericReportRow.getRejectCode() + " - " + genericReportRow.getDetailReject()).getAndAdd(1);
                                    } else {
                                        Stats.getRejectCounter().put(genericReportRow.getRejectCode() + " - " + genericReportRow.getDetailReject(), new AtomicInteger(1));
                                    }
                                    Stats.addNumberPliNotValid(1);
                                    Stats.addNumberDocumentNotValid(genericReportRow.getFold().getData().size());
                                })
                                .map(GenericReportRow::getFold)) ;
                Logger.info("Traitement des rejets");

                final Timer closeInfiniteStream = new Timer(true);
                closeInfiniteStream.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if ((Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get() == Stats.getNumberDocumentEntry().get()))
                            rejectQueueX.close();
                    }
                }, 2000, 5000);

                GenericReport.createReport(Config.getMainConfig().getReportsFolder(),
                        Config.getMainConfig().getTempFilesNumberOfLines(),
                        Config.getReports(),
                        concatStream
                );
                java.nio.file.Files.write(Paths.get(Config.getMainConfig().getReportsFolder() + File.separator + "compteur.txt"), Stats.resume().getBytes());
                Logger.info("Fin du traitement");
                Logger.info(Stats.resume());
                System.exit(0);
            }

        } catch (IOException | ExgedMainException | ExgedParserException e) {
            e.printStackTrace();
        }
    }

    private static String detectDate(String date) {
        return date.substring(0, 4) + "_" + date.substring(4, 6);
    }
}
