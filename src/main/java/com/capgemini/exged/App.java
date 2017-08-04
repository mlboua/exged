package com.capgemini.exged;

import com.beust.jcommander.JCommander;
import com.google.common.io.Files;
import config.json.mapping.mainConfig.Config;
import creator.GenericCreator;
import data.Data;
import data.Fold;
import engine.TemplateEngineExecutor;
import exception.ExgedCoreException;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;
import initialisation.Args;
import initialisation.InitFolders;
import reader.csv.CsvReader;
import reader.json.JsonConfigReader;
import reader.yaml.YamlConfigReader;
import stats.Stats;
import validator.DetailReject;
import validator.GenericValidator;
import validator.Reject;
import writer.xml.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    public static final Stats stats = new Stats();
    public static Config config;
    public static long startTime;

    public static void main(String... args) {
        try {
            initialisation(args);

            List<CsvIdentifier> identifierList = JsonConfigReader.readJsonIdentifier(new File("config/foldDetection.json")).stream().map(CsvIdentifier::new).collect(Collectors.toList());

            final GenericValidator genericValidator = new GenericValidator(JsonConfigReader.readJsonMappingHeaders(new File("config/mappingHeaders.json")),
                    JsonConfigReader.readJsonMappingReject(new File("config/mappingReject.json")));

            final GenericCreator genericCreator = new GenericCreator(JsonConfigReader.readJsonMappingCreator(new File("config/mappingCreator.json")));

            CsvReader reader = new CsvReader(true, identifierList);

            Map<Optional<Reject>, List<Fold>> data;
            final TemplateEngineExecutor templateEngineExecutor = new TemplateEngineExecutor(config);
            final CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
            Stream.Builder<List<DetailReject>> detailRejectStream = Stream.builder();

            stats.addNumberDocumentEntry(reader.countRowsFolder(new File(config.getSplittedTempFolder())).intValue());
            stats.addNumberDocumentExit(1);
            startTime = System.currentTimeMillis();
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new MyTimerTask(), 1000, 2000);

            reader.readFolderParallel(new File(config.getSplittedTempFolder())) // Read lines to -> List<List<String>>
                    .flatMap(Data::foldStream) // FilesRows -> Rows -> Folds
                    .filter(fold -> {
                        final Optional<List<DetailReject>> rejects = genericValidator.validateFold(fold);
                        if (rejects.isPresent()) {
                            detailRejectStream.add(rejects.get());
                            return false;
                        }
                        return true;
                    })
                    .map(fold -> genericCreator.createFields(fold, fold.getHeader()))  // Création des valeurs complémentaires
                    .forEach(fold -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("pli", fold.getData());
                        params.put("headers", fold.getHeader());
                        final Optional<String> render = templateEngineExecutor.render(params);
                        if (render.isPresent()) {
                            try {
                                File xmlFile = new File(config.getXmlTempFolder() + File.separator
                                        + detectDate(fold.getData().get(0).get(fold.getHeader().get("DB_DATE_NUM"))) + File.separator
                                        + fold.getId() + ".xml");
                                Files.createParentDirs(xmlFile);
                                Files.write(XMLUtils.toPrettyString(render.get(), 2).getBytes(), xmlFile);
                                stats.addNumberDocumentExit(fold.getData().size());
                                //return Optional.of(xmlFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //return Optional.<File>empty();
                    });
            detailRejectStream.build().forEach(detailRejects -> {
                try {
                    Files.write(detailRejects.toString().getBytes(), new File("report.md"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //System.out.println(Arrays.stream(Package.getPackages()).filter(aPackage -> aPackage.getName().contains("validator")).collect(Collectors.toList()));
            timer.cancel();
            System.out.println();
            System.out.println(stats);
        } catch (ExgedParserException | IOException | ExgedCoreException e) {
            e.printStackTrace();
        }
    }

    private static String detectDate(String date) {
        return date.substring(0, 4) + "_" + date.substring(4, 6);
    }

    private static void initialisation(String... args) throws IOException, ExgedCoreException {
        // Lectures des arguments passer en paramètre
        final Args arguments = new Args();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        // Chargement du fichier de configuration en fonctions des
        // paramètres d'entrées
        final File configFile = new File(arguments.getConfigPath() != null
                ? arguments.getConfigPath()
                : "config.yaml");

        // Vérification de l'existance du fichier de configuration
        if (!configFile.exists()) {
            throw new ExgedCoreException("Fichier de configuration introuvable, veuillez le renseigner en argument avec \"-c\"");
        }
        // Lecture du fichier de configuration
        config = YamlConfigReader.readYamlConfig(configFile);
        config.init();
        InitFolders.init(config);
    }

    private static class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            long eta = stats.getNumberDocumentExit() == 0 ? 0 :
                    (stats.getNumberDocumentEntry() - stats.getNumberDocumentExit()) * (System.currentTimeMillis() - startTime) / stats.getNumberDocumentExit();

            String etaHms = stats.getNumberDocumentExit() == 0 ? "N/A" :
                    String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                            TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

            StringBuilder string = new StringBuilder(140);
            int percent = (int) (stats.getNumberDocumentExit() * 100 / stats.getNumberDocumentEntry());
            string
                    .append('\r')
                    .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                    .append(String.format(" %d%% [", percent))
                    .append(String.join("", Collections.nCopies(percent, "=")))
                    .append('>')
                    .append(String.join("", Collections.nCopies(100 - percent, " ")))
                    .append(']')
                    .append(String.join("", Collections.nCopies((int) (Math.log10(stats.getNumberDocumentEntry())) - (int) (Math.log10(stats.getNumberDocumentExit())), " ")))
                    .append(String.format(" %d/%d, ETA: %s,RAM(MB): %d/%d", stats.getNumberDocumentExit(), stats.getNumberDocumentEntry(), etaHms, (int) (Runtime.getRuntime().totalMemory() / 1048576.0), (int) (Runtime.getRuntime().maxMemory() / 1048576.0)));
            System.out.print(string);
        }
    }
}
