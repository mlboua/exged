package com.capgemini.exged;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.pmw.tinylog.Logger;

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
import data.Fold;
import data.FoldStatus;
import engine.TemplateEngineExecutor;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import reader.csv.CsvReader;
import reader.csv.StreamCsvParser;
import report.GenericReport;
import reports.GenericReportRow;
import stats.Stats;
import validator.DetailReject;
import validator.GenericValidator;
import writer.xml.XMLUtils;

public class App {

    public static void main(final String... args) {
        try {
            Logger.info("Initialisation");
            Stats.init();
            // Chargement en mémoire des fichiers de configuration json/yaml
            ConfigInitializer.initConfig(args);
            // Préparation du lecteur de fichier CSV
            final StreamCsvParser streamCsvParser = new StreamCsvParser(
                    Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList())); // Parser avec SimpleFlatMapper

            final CsvReader reader = new CsvReader(true, // Parser avec Univocity pour splitter les fichiers d'entrée
                    Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()));

            // Moteur de template
            final TemplateEngineExecutor templateEngineExecutor = new TemplateEngineExecutor(Config.getMainConfig());

            // Validateur et créateur
            final GenericValidator genericValidator = new GenericValidator(Config.getValidators(), Config.getRejects());
            final GenericCreator genericCreator = new GenericCreator(Config.getCreators());

            // Ajout des stats d'entrée
            Stats.addNumberDocumentEntry(
                    reader.countRowsFolder(new File(Config.getMainConfig().getInputFolder())).intValue());
            Stats.addNumberFilesEntry(
                    (int) java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder())).count());

            Logger.info("Nettoyage du dossier temporaire");
            java.nio.file.Files.list(Paths.get(Config.getMainConfig().getSplittedTempFolder())).forEach(path -> {
                try {
                    java.nio.file.Files.delete(path);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            // Fragmentation des fichiers
            Logger.info("Fragmentation des fichiers");
            splitFiles();

            final Boolean isProd = Config.getMainConfig().getMode().equalsIgnoreCase("prod");
            final Queue<GenericReportRow> rejectQueueX = QueueFactories.<GenericReportRow>unboundedQueue().build(); // Queue Infinie contenant les rejets

            // Lancement des stats dans la console
            final Timer treatmentTimer = new Timer(true);
            treatmentTimer.scheduleAtFixedRate(new TreatmentTask(), 2000, 30000);
            // Traitement des fichiers fragmenté
            Logger.info("Lancement du traitement");
            try (final FutureStream<Fold> folds = new LazyReact(100, 100).fromStream(
                    streamCsvParser.readFolderParallel(new File(Config.getMainConfig().getSplittedTempFolder())))) {

                final Stream<Fold> concatStream = folds
                        .map(fold -> new Tuple2<>(fold, genericValidator.validateFold(fold))) // créatoin
                        .peek((final Tuple2<Fold, Optional<List<DetailReject>>> tupleFoldReject) -> { // Validation des plis
                            Stats.addNumberPliEntry(1);
                            final List<String> inList = new ArrayList<>();
                            tupleFoldReject.v2.ifPresent(rejectList -> {
                                rejectList.forEach(reject -> {
                                    if (reject.getValues().isPresent()) {
                                        if (!inList.contains(tupleFoldReject.v1.getId())) {// verifier si le document est déà rejeté, à faire autrement
                                            rejectQueueX.add(new GenericReportRow(reject.getCode(),
                                                    reject.getDetail() + ": " + reject.getValues().get(),
                                                    tupleFoldReject.v1));
                                            inList.add(tupleFoldReject.v1.getId());
                                        }
                                    } else {
                                        rejectQueueX.add(new GenericReportRow(reject.getCode(), reject.getDetail(),
                                                tupleFoldReject.v1));
                                    }
                                });
                            });
                        }).filter(tupleFoldReject -> !tupleFoldReject.v2.isPresent()) // Enlève les plis rejetés
                        .map(Tuple2::v1) // Supprime le tuple pour avoir le pli uniquement
                        .peek(genericCreator::createFields) // Création des valeurs supplémentaire
                        .peek(fold -> fold.setId(fold.getValue(0, "ID_PLI"))) // Changement de l'ID
                        .async() // OP Asynchrone pour améliorer les performances
                        .map(fold -> { // Création du rendu avec le moteur de template (Rythm engine)
                            final Map<String, Object> params = new HashMap<>();
                            params.put("pli", fold.getData());
                            params.put("headers", fold.getHeader());
                            return new Tuple2<>(fold, templateEngineExecutor.render(params));
                        }).sync() // Opérations synchrone
                        .filter(tupleFoldRender -> { // Rejets des plis ayant une erreur pendant le rendu
                            if (tupleFoldRender.v2.isPresent()) {
                                return true;
                            } else {
                                rejectQueueX.add(new GenericReportRow("Render-1",
                                        "Erreur lors de l'appel du générateur de template", tupleFoldRender.v1));
                                return false;
                            }
                        }).map(tupleFoldRender -> {
                            return new Tuple3<>(tupleFoldRender.v1, // Création de l'objet File correspondant au fichier de sorti

                                    tupleFoldRender.v2,
                                    new File(Config.getMainConfig().getOutputFolder() + File.separator
                                            + detectDate(tupleFoldRender.v1.getValue(0, "DB_DATE_NUM")) + File.separator
                                            + tupleFoldRender.v1.getId() + ".xml"));
                        }).async() // Opération asynchrone car l'I/O est utilisé
                        .peek(tupleFoldRenderFile -> { // Créations de sous dossiers
                            try {
                                java.nio.file.Files.createDirectories(
                                        Paths.get(Config.getMainConfig().getOutputFolder() + File.separator
                                                + detectDate(tupleFoldRenderFile.v1.getValue(0, "DB_DATE_NUM"))));
                            } catch (final IOException e) {
                                Logger.error("Impossible de créer les dossiers parent au fichier d'output: " + e);
                            }
                        }).filter(tupleFoldRenderFile -> {// supprimer les documents non valide avec le XSD
                            if (XMLUtils.validateAgainstXSD(tupleFoldRenderFile.v2.orElseGet(null),
                                    Config.getMainConfig().getXsdValidator())) {
                                return true;
                            } else {
                                rejectQueueX.add(new GenericReportRow("Render-2",
                                        "Erreur de validation du XML avec le XSD", tupleFoldRenderFile.v1));
                                return false;
                            }
                        }).peek(tupleFoldRenderFile -> { // Ecriture du fichier XML sur le disque dur
                            try {
                                java.nio.file.Files.write(tupleFoldRenderFile.v3.toPath(),
                                        XMLUtils.toPrettyString(tupleFoldRenderFile.v2.orElseGet(null), 4).getBytes());
                            } catch (final IOException e) {
                                Logger.error("Impossible d'écrire sur le système: " + e);
                            }
                        }).peek(tupleFoldRenderFile -> { // Copie des fichiers supplémentaire
                            try {
                                if (isProd) {
                                    Files.copy(
                                            new File(Config.getMainConfig().getExternFilesPath() + File.separator
                                                    + FilenameUtils
                                                            .normalize(tupleFoldRenderFile.v1.getValue(0, "FLENAMED"))),
                                            new File(Config.getMainConfig().getOutputFolder() + File.separator
                                                    + detectDate(tupleFoldRenderFile.v1.getValue(0, "DB_DATE_NUM"))
                                                    + File.separator + tupleFoldRenderFile.v1.getId() + ".tif"));
                                }
                                tupleFoldRenderFile.v1.setStatus(FoldStatus.TREATED);
                                Stats.addNumberDocumentExit(tupleFoldRenderFile.v1.getData().size());
                                Stats.addNumberPliExit(1);
                                Stats.addNumberFileExit(1);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }).sync().map(Tuple3::v1);

                final Timer closeInfiniteStream = new Timer(true);
                closeInfiniteStream.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (Stats.getNumberDocumentExit().get() + Stats.getNumberDocumentNotValid().get() == Stats
                                .getNumberDocumentEntry().get()) {
                            rejectQueueX.close();
                        }
                    }
                }, 2000, 5000);

                GenericReport.createReport(Config.getMainConfig().getReportsFolder(),
                        Config.getMainConfig().getTempFilesNumberOfLines(), Config.getReports(), concatStream);

                rejectQueueX.disconnectStreams(1);
                //System.out.println("Nombre de reject : " + rejectQueueX.size());
                GenericReport.createReport(Config.getMainConfig().getReportsFolder(),
                        Config.getMainConfig().getTempFilesNumberOfLines(), Config.getReports(),
                        rejectQueueX.stream()
                                .peek(genericReportRow -> genericReportRow.getFold().setStatus(FoldStatus.REJECT))
                                .peek(genericReportRow -> {
                                    genericReportRow.getFold().getData().forEach(row -> {
                                        row.add(genericReportRow.getRejectCode() + " - "
                                                + genericReportRow.getDetailReject());
                                    });
                                    genericReportRow.getFold().getHeader().put("EXGED_ERRORS",
                                            genericReportRow.getFold().getData().get(0).size() - 1);
                                    if (Stats.getRejectCounter().containsKey(genericReportRow.getRejectCode() + " - "
                                            + genericReportRow.getDetailReject())) {
                                        Stats.getRejectCounter().get(genericReportRow.getRejectCode() + " - "
                                                + genericReportRow.getDetailReject()).getAndAdd(1);
                                    } else {
                                        Stats.getRejectCounter().put(genericReportRow.getRejectCode() + " - "
                                                + genericReportRow.getDetailReject(), new AtomicInteger(1));
                                    }
                                    Stats.addNumberPliNotValid(1);
                                    Stats.addNumberDocumentNotValid(genericReportRow.getFold().getData().size());
                                }).map(GenericReportRow::getFold));

                java.nio.file.Files.write(
                        Paths.get(Config.getMainConfig().getReportsFolder() + File.separator + "compteur.txt"),
                        Stats.resume().getBytes());

                Logger.info("Fin du traitement");
                Logger.info(Stats.resume());
                System.exit(0);
            }

        } catch (IOException | ExgedMainException | ExgedParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fragmente les fichiers en fonction de la Config
     *
     * @throws IOException
     */
    private static void splitFiles() throws IOException {
        final Timer spitTimer = new Timer(true);
        spitTimer.scheduleAtFixedRate(new SplitTask(), 2000, 3000);

        final CsvReader reader = new CsvReader(true,
                Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList())); // Parser avec Univocity

        java.nio.file.Files.list(Paths.get(Config.getMainConfig().getInputFolder())).forEach(path -> {
            try {
                reader.splitFile(path.toFile(), new File(Config.getMainConfig().getSplittedTempFolder()),
                        Config.getMainConfig().getTempFilesNumberOfLines(),
                        Config.getIdentifiers().stream().map(CsvIdentifier::new).collect(Collectors.toList()),
                        Stats.getNumberSplittedFiles());
            } catch (final ExgedParserException e) {
                e.printStackTrace();
            }
        });
        spitTimer.cancel();
    }

    private static String detectDate(final String date) {
        return date.substring(0, 4) + "_" + date.substring(4, 6);
    }
}
