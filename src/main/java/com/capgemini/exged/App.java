package com.capgemini.exged;

import com.capgemini.exged.exception.ExgedMainException;
import com.capgemini.exged.initialiser.ConfigInitialiser;
import com.google.common.io.Files;
import creator.GenericCreator;
import cyclops.collections.mutable.QueueX;
import cyclops.stream.FutureStream;
import data.Fold;
import data.FoldStatus;
import engine.TemplateEngineExecutor;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import org.jooq.lambda.tuple.Tuple2;
import org.pmw.tinylog.Logger;
import reader.config.Config;
import reader.csv.CsvReader;
import reader.csv.StreamCsvParser;
import reports.GenericReportRow;
import stats.Stats;
import validator.DetailReject;
import validator.GenericValidator;
import writer.xml.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    public static void main(String... args) {
        try {
            Logger.info("Initialisation");
            Stats.init();
            // Chargement en mémoire des fichiers de configuration json/yaml
            ConfigInitialiser.initConfig(args);
            // Préparation du lecteur de fichier CSV
            final CsvReader reader = new CsvReader(true,
                    Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));
            // Moteur de template
            final TemplateEngineExecutor templateEngineExecutor = new TemplateEngineExecutor(Config.getMainConfig());
            // Stream de Rejects
            Stream.Builder<GenericReportRow> rejectStream = Stream.builder();

            final GenericValidator genericValidator = new GenericValidator(Config.getValidators(), Config.getRejects());
            final GenericCreator genericCreator = new GenericCreator(Config.getCreators());

            // Ajout des stats d'entrée
            Stats.addNumberDocumentEntry(reader.countRowsFolder(new File(Config.getMainConfig().getInputFolder())).intValue());
            Stats.addNumberFilesEntry((int) java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder())).count());

            // Lancement des stats dans la console
            //final Timer timer = new Timer(true);
            //timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 1000);

            final QueueX<GenericReportRow> rejectQueueX = QueueX.<GenericReportRow>empty().lazy();

            Logger.info("Lancement du traitement");

            StreamCsvParser streamCsvParser = new StreamCsvParser(Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));
            try (FutureStream<Fold> folds = streamCsvParser.readFolderParallel(new File(Config.getMainConfig().getInputFolder()))) {
                folds.map(fold -> new Tuple2<>(fold, genericValidator.validateFold(fold)))
                        .peek((Tuple2<Fold, Optional<List<DetailReject>>> tupleFoldReject) ->
                                tupleFoldReject.v2.ifPresent(rejectList -> {
                                    Stats.addNumberPliNotValid(1);
                                    Stats.addNumberDocumentNotValid(tupleFoldReject.v1.getData().size());
                                    rejectList.forEach(reject -> {
                                        if (reject.getValues().isPresent()) {
                                            rejectQueueX.plus(new GenericReportRow(reject.getCode(), reject.getDetail() + ": " + reject.getValues().get(), tupleFoldReject.v1));
                                        } else {
                                            rejectQueueX.plus(new GenericReportRow(reject.getCode(), reject.getDetail(), tupleFoldReject.v1));
                                        }
                                    });
                                })
                        )
                        .filterNot(tupleFoldReject -> tupleFoldReject.v2.isPresent())
                        .map(Tuple2::v1)
                        .peek(genericCreator::createFields)
                        .peek(fold -> fold.setId(fold.getValue(0, "ID_PLI")))
                        .parallel(foldStream ->
                                foldStream.map(fold -> {
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("pli", fold.getData());
                                    params.put("headers", fold.getHeader());
                                    return new Tuple2<>(fold, templateEngineExecutor.render(params));
                                })
                        )
                        .filter(tupleFoldRender -> {
                            if (tupleFoldRender.v2.isPresent()) {
                                return true;
                            } else {
                                tupleFoldRender.v1.setStatus(FoldStatus.REJECT);
                                rejectQueueX.plus(new GenericReportRow("Render-1", "Erreur lors de l'appel du générateur de template", tupleFoldRender.v1));
                                return false;
                            }
                        })
                        .peek(tupleFold -> {
                            try {
                                File xmlFile = new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                        + detectDate(tupleFold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                        + tupleFold.v1.getId() + ".xml");
                                Files.createParentDirs(xmlFile);
                                Files.write(XMLUtils.toPrettyString(tupleFold.v2.orElseGet(null), 2).getBytes(), xmlFile);
                                if (Config.getMainConfig().getMode().equalsIgnoreCase("prod")) {
                                    Files.copy(new File(Config.getMainConfig().getExternFilesPath() + File.separator +
                                                    tupleFold.v1.getValue(0, "FLENAMED")),
                                            new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                                    + detectDate(tupleFold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                                    + tupleFold.v1.getId() + ".tif"));
                                }
                                Stats.addNumberDocumentExit(tupleFold.v1.getData().size());
                                Stats.addNumberPliExit(1);
                                Stats.addNumberFileExit(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .size();
                Logger.info("Fin du traitement");
            }






            /*java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder()))
                    .forEach(path -> {
                        try {
                            reader.splitFile(path.toFile(), new File(Config.getMainConfig().getSplittedTempFolder()),
                                    Config.getMainConfig().getTempFilesNumberOfLines(), Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));
                        } catch (ExgedParserException e) {
                            e.printStackTrace();
                        }
                    });*/

            /*

            final FutureStream<Tuple2<Fold, Optional<List<DetailReject>>>> validatedTupleStream = new LazyReact(8, 16)
                    .fromStream(streamCsvParser.readFolderParallel(new File(Config.getMainConfig().getInputFolder())).stream())
                    .withAsync(true)
                    .map(fold -> new Tuple2<>(fold, genericValidator.validateFold(fold)))
                    .peek(fold -> {
                        if (!fold.v2.isPresent()) {
                            genericCreator.createFields(fold.v1);
                            Map<String, Object> params = new HashMap<>();
                            params.put("pli", fold.v1.getData());
                            params.put("headers", fold.v1.getHeader());
                            final Optional<String> render = templateEngineExecutor.render(params);
                            if (render.isPresent()) {
                                try {
                                    File xmlFile = new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                            + detectDate(fold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                            + fold.v1.getId() + ".xml");
                                    Files.createParentDirs(xmlFile);
                                    Files.write(XMLUtils.toPrettyString(render.get(), 2).getBytes(), xmlFile);
                                    if (Config.getMainConfig().getMode().equalsIgnoreCase("prod")) {
                                        Files.copy(new File(Config.getMainConfig().getExternFilesPath() + File.separator +
                                                        fold.v1.getValue(0, "FLENAMED")),
                                                new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                                        + detectDate(fold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                                        + fold.v1.getId() + ".tif"));
                                    }
                                    Stats.addNumberDocumentExit(fold.v1.getData().size());
                                    Stats.addNumberPliExit(1);
                                    Stats.addNumberFileExit(1);
                                    fold.v1.setStatus(FoldStatus.ACCEPT);
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        fold.v1.setStatus(FoldStatus.REJECT);
                    });

            System.out.println(validatedTupleStream.size());*/
            //.splitBy(tupleFoldValidation -> !tupleFoldValidation.v2.isPresent());
                    /*.map1(acceptedFold -> QueueX.queueX(acceptedFold.map(tupleFold -> {
                        tupleFold.v1.setStatus(FoldStatus.ACCEPT);
                        return tupleFold.v1;
                    })).lazy())
                    .map2(rejectFold -> QueueX.queueX(rejectFold.map(tupleFold -> {
                        tupleFold.v1.setStatus(FoldStatus.REJECT);
                        return new Tuple2<>(tupleFold.v1, tupleFold.v2.orElseGet(ArrayList::new));
                    })).lazy());*/

            //System.out.println(validatedTupleStream.v1.size());

            // Lit les fichiers, rassemble les plis et fait la validation puis les sépare en deux groupe -> v1=valide, v2=non valide
                    /*.stream()
                    .map(fold -> new Tuple2<>(fold, genericValidator.validateFold(fold))
                    .splitBy(tupleFoldValidation -> !tupleFoldValidation.v2.isPresent())
                    .map1(acceptedFold -> QueueX.queueX(acceptedFold.map(tupleFold -> {
                        tupleFold.v1.setStatus(FoldStatus.ACCEPT);
                        return tupleFold.v1;
                    })).lazy())
                    .map2(rejectFold -> QueueX.queueX(rejectFold.map(tupleFold -> {
                        tupleFold.v1.setStatus(FoldStatus.REJECT);
                        return new Tuple2<>(tupleFold.v1, tupleFold.v2.orElseGet(ArrayList::new));
                    })).lazy());
            System.out.println(QueueFolds.v1.stream().size());
            QueueFolds.v1.stream().peek(genericCreator::createFields)
                    .peek(fold -> fold.setId(fold.getValue(0, "ID_PLI")))
                    .parallel(foldStream ->
                            foldStream.map(fold -> {
                                Map<String, Object> params = new HashMap<>();
                                params.put("pli", fold.getData());
                                params.put("headers", fold.getHeader());
                                final Optional<String> render = templateEngineExecutor.render(params);
                                return new Tuple2<>(fold, render);
                            })
                    )
                    .filter(tupleFoldRender -> {
                        if (tupleFoldRender.v2.isPresent()) {
                            return true;
                        } else {
                            tupleFoldRender.v1.setStatus(FoldStatus.REJECT);
                            //tupleStream.v2.append(tupleFoldRender.v1);
                            return false;
                        }
                    })
                    .parallel(foldFunction ->
                            foldFunction.peek(tupleFold -> {
                                try {
                                    File xmlFile = new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                            + detectDate(tupleFold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                            + tupleFold.v1.getId() + ".xml");
                                    Files.createParentDirs(xmlFile);
                                    Files.write(XMLUtils.toPrettyString(tupleFold.v2.orElseGet(null), 2).getBytes(), xmlFile);
                                    if (Config.getMainConfig().getMode().equalsIgnoreCase("prod")) {
                                        Files.copy(new File(Config.getMainConfig().getExternFilesPath() + File.separator +
                                                        tupleFold.v1.getValue(0, "FLENAMED")),
                                                new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                                        + detectDate(tupleFold.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                                        + tupleFold.v1.getId() + ".tif"));
                                    }
                                    stats.addNumberDocumentExit(tupleFold.v1.getData().size());
                                    stats.addNumberPliExit(1);
                                    stats.addNumberFileExit(1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }));

*/
            /*final Topic<Fold> foldTopic = new Topic<>();
            final ReactiveSeq<Fold> foldStream = ReactiveSeq.<Fold>concat(tupleStream.v1(), tupleStream.v2());
            foldTopic.fromStream(foldStream);
            GenericReport.createReport(Config.getMainConfig().getReportsFolder(), Config.getReports(), foldTopic);*/


  /*          //Traitement
            final List<Fold> foldList = reader.readFolderParallel(new File(Config.getMainConfig().getInputFolder()))         // Read lines to -> List<List<String>>
                    .flatMap(Data::foldStream)                                          // FilesRows -> Rows -> Folds
                    .filter(fold -> {                                                   // Validation
                        stats.addNumberPliEntry(1);
                        final Optional<List<DetailReject>> rejects = genericValidator.validateFold(fold);
                        if (rejects.isPresent()) {
                            stats.addNumberPliNotValid(1);
                            stats.addNumberDocumentNotValid(fold.getData().size());
                            rejects.get().forEach(reject -> {
                                if (reject.getValues().isPresent()) {
                                    rejectStream.add(new GenericReportRow(reject.getCode(), reject.getDetail() + ": " + reject.getValues().get(), fold));
                                } else {
                                    rejectStream.add(new GenericReportRow(reject.getCode(), reject.getDetail(), fold));
                                }
                            });
                            return false;
                        }
                        return true;
                    })
                    .peek(genericCreator::createFields)  // Création des valeurs complémentaires
                    .peek(fold -> fold.setId(fold.getValue(0, "ID_PLI"))) // Changement de l'ID  du pli
                    .peek(fold -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("pli", fold.getData());
                        params.put("headers", fold.getHeader());
                        final Optional<String> render = templateEngineExecutor.render(params);
                        if (render.isPresent()) {
                            try {
                                File xmlFile = new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                        + detectDate(fold.getValue(0, "DB_DATE_NUM")) + File.separator
                                        + fold.getId() + ".xml");
                                Files.createParentDirs(xmlFile);
                                Files.write(XMLUtils.toPrettyString(render.get(), 2).getBytes(), xmlFile);
                                /*Files.copy(new File(Config.getMainConfig().getExternFilesPath() + File.separator +
                                                fold.getValue(0, "FLENAMED")),
                                        new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                                + detectDate(fold.getValue(0, "DB_DATE_NUM")) + File.separator
                                                + fold.getId() + ".tif"));
                                if (xmlFile.exists()) {
                                    stats.addNumberDocumentExit(fold.getData().size());
                                    stats.addNumberPliExit(1);
                                    stats.addNumberFileExit(1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .collect(Collectors.toList());
*/
/*
            GenericReport.createReport(Config.getMainConfig().getReportsFolder(), Config.getReports(), foldList);

            if (!foldList.isEmpty()) {
                CsvWrite.writeCsvFile(new File("traceFold.csv"), foldList.get(0).getHeader().keySet().toArray(new String[foldList.get(0).getHeader().keySet().size()]), foldList.stream().flatMap(fold -> fold.getData().stream().map(row -> row.toArray(new String[row.size()]))).collect(Collectors.toList()));
            }
            //timer.cancel();
            String[] headers = {"DOCIDX", "DOCIDXGED", "DB_NUMPLI", "FILENAMED", "ID_PLI", "CODE_REJET", "DETAIL_REJET"};
            final List<GenericReportRow> genericReportRowList = rejectStream.build().collect(Collectors.toList());
            final List<String[]> rejectList = genericReportRowList.stream()
                    .map(reject -> {
                        final List<String> rejectListTemp = new ArrayList<>();
                        rejectListTemp.add(getValue(reject.getFold(), "DOCIDX"));
                        rejectListTemp.add(getValue(reject.getFold(), "DOCIDXGED"));
                        rejectListTemp.add(getValue(reject.getFold(), "DB_NUMPLI"));
                        rejectListTemp.add(getValue(reject.getFold(), "FLENAMED"));
                        rejectListTemp.add(reject.getRejectCode());
                        rejectListTemp.add(reject.getDetailReject());
                        return rejectListTemp.toArray(new String[rejectListTemp.size()]);
                    }).collect(Collectors.toList());
            CsvReportWriter.createCounter(new File("compteur.txt"),
                    stats,
                    genericReportRowList.stream()
                            .collect(Collectors.groupingBy(GenericReportRow::getRejectCode,
                                    Collectors.groupingBy(GenericReportRow::getDetailReject))));
            if (!rejectList.isEmpty()) {
                CsvWrite.writeCsvFile(new File("report.csv"), headers, rejectList);
            }
*/
            System.out.println(Stats.resume());
        } catch (IOException | ExgedMainException | ExgedParserException e) {
            e.printStackTrace();
        }
    }

    private static String getValue(Fold fold, String value) {
        return fold.getData().get(0).get(fold.getHeader().get(value));
    }

    private static String detectDate(String date) {
        return date.substring(0, 4) + "_" + date.substring(4, 6);
    }

    private static class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            long eta = Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get() == 0 ? 0 :
                    ((int) Stats.getNumberDocumentEntry().get() - ((int) Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get())) * Duration.between(Stats.getInstantStart(), Instant.now()).toMillis() / ((int) Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get());

            String etaHms = Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get() == 0 ? "N/A" :
                    String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                            TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

            StringBuilder consoleMsg = new StringBuilder(140);
            int percent = (int) ((Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()) * 100 / Stats.getNumberDocumentEntry().get());
            consoleMsg.append('\r')
                    .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                    .append(String.format(" %d%% [", percent))
                    .append(String.join("", Collections.nCopies(percent, "=")))
                    .append('>')
                    .append(String.join("", Collections.nCopies(100 - percent, " ")))
                    .append(']')
                    .append(String.join("", Collections.nCopies((int) (Math.log10(Stats.getNumberDocumentEntry().get())) - ((int) (Math.log10(Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()))),
                            " ")))
                    .append(String.format(" %d/%d, ETA: %s, Row/sec: %d/sec, RAM(MB): %d/%d",
                            (Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get()), Stats.getNumberDocumentEntry().get(), etaHms,
                            (int) Stats.getNumberFileTreatedPerSeconds(), (int) (Runtime.getRuntime().totalMemory() / 1048576.0),
                            (int) (Runtime.getRuntime().maxMemory() / 1048576.0)));
            System.out.print(consoleMsg);
        }
    }
}
