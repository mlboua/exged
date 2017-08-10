package com.capgemini.exged;

import com.capgemini.exged.config.Config;
import com.capgemini.exged.exception.ExgedMainException;
import com.capgemini.exged.initialiser.ConfigInitialiser;
import com.google.common.io.Files;
import creator.GenericCreator;
import data.Data;
import data.Fold;
import engine.TemplateEngineExecutor;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import reader.csv.CsvReader;
import reports.GenericReportRow;
import stats.Stats;
import validator.DetailReject;
import validator.GenericValidator;
import writer.csv.CsvWrite;
import writer.reports.csv.CsvReportWriter;
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

    static final Stats stats = new Stats();
        
    public static void main(String... args) {
        try {
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
            stats.addNumberDocumentEntry(reader.countRowsFolder(new File(Config.getMainConfig().getSplittedTempFolder())).intValue());
            stats.addNumberFilesEntry((int) java.nio.file.Files.list(Paths.get(Config.getMainConfig().getSplittedTempFolder())).count() - 1);

            // Lancement des stats dans la console
            final Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 1000);

            //Traitement
            final List<Fold> foldList = reader.readFolderParallel(new File(Config.getMainConfig().getSplittedTempFolder()))         // Read lines to -> List<List<String>>
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
                    .map(fold -> genericCreator.createFields(fold, fold.getHeader()))  // Création des valeurs complémentaires
                    .peek(fold -> fold.setId(fold.getData().get(0).get(fold.getHeader().get("ID_PLI")))) // Changement de l'ID  du pli
                    .peek(fold -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("pli", fold.getData());
                        params.put("validations", fold.getHeader());
                        final Optional<String> render = templateEngineExecutor.render(params);
                        if (render.isPresent()) {
                            try {
                                File xmlFile = new File(Config.getMainConfig().getOutputTempFolder() + File.separator
                                        + detectDate(fold.getData().get(0).get(fold.getHeader().get("DB_DATE_NUM"))) + File.separator
                                        + fold.getId() + ".xml");
                                Files.createParentDirs(xmlFile);
                                Files.write(XMLUtils.toPrettyString(render.get(), 2).getBytes(), xmlFile);
                                if (xmlFile.exists()) {
                                    stats.addNumberDocumentExit(fold.getData().size());
                                    stats.addNumberPliExit(1);
                                    stats.addNumberFileExit(1);
                                    //return Optional.of(xmlFile);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //return Optional.<File>empty();
                    })
                    .collect(Collectors.toList());

            CsvWrite.writeCsvFile(new File("traceFold.csv"), foldList.get(0).getHeader().keySet().toArray(new String[foldList.get(0).getHeader().keySet().size()]), foldList.stream().flatMap(fold -> fold.getData().stream().map(row -> row.toArray(new String[row.size()]))).collect(Collectors.toList()));
            timer.cancel();
            System.out.println();
            System.out.println("Création des fichiers de rapports");

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
            CsvWrite.writeCsvFile(new File("report.csv"), headers, rejectList);

            System.out.println(stats);
        } catch (ExgedParserException | IOException | ExgedMainException e) {
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
            long eta = stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get() == 0 ? 0 :
                    ((int) stats.getNumberDocumentEntry().get() - ((int) stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get())) * Duration.between(stats.getInstantStart(), Instant.now()).toMillis() / ((int) stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get());

            String etaHms = stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get() == 0 ? "N/A" :
                    String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                            TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

            StringBuilder consoleMsg = new StringBuilder(140);
            int percent = (int) ((stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get()) * 100 / stats.getNumberDocumentEntry().get());
            consoleMsg.append('\r')
                    .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                    .append(String.format(" %d%% [", percent))
                    .append(String.join("", Collections.nCopies(percent, "=")))
                    .append('>')
                    .append(String.join("", Collections.nCopies(100 - percent, " ")))
                    .append(']')
                    .append(String.join("", Collections.nCopies((int) (Math.log10(stats.getNumberDocumentEntry().get())) - ((int) (Math.log10(stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get()))),
                            " ")))
                    .append(String.format(" %d/%d, ETA: %s, Row/sec: %d/sec, RAM(MB): %d/%d",
                            (stats.getNumberDocumentExit().get() + stats.getNumberDocumentNotValid().get()), stats.getNumberDocumentEntry().get(), etaHms,
                            (int) stats.getNumberFileTreatedPerSeconds(), (int) (Runtime.getRuntime().totalMemory() / 1048576.0),
                            (int) (Runtime.getRuntime().maxMemory() / 1048576.0)));
            System.out.print(consoleMsg);
        }
    }
}
