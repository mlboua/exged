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
import initialisation.Args;
import initialisation.InitFolders;
import reader.csv.CsvReader;
import reader.json.JsonConfigReader;
import reader.yaml.YamlConfigReader;
import stats.Stats;
import validator.GenericValidator;
import validator.Reject;
import writer.xml.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class App {

    public static final Stats stats = new Stats();
    public static Config config;

    public static void main(String... args) {
        try {
            initialisation(args);

            List<CsvIdentifier> identifierList = JsonConfigReader.readJsonIdentifier(new File("config/foldDetection.json")).stream().map(CsvIdentifier::new).collect(Collectors.toList());

            new GenericValidator(JsonConfigReader.readJsonMappingHeaders(new File("config/mappingHeaders.json")),
                    JsonConfigReader.readJsonMappingReject(new File("config/mappingReject.json")));

            final GenericCreator genericCreator = new GenericCreator(JsonConfigReader.readJsonMappingCreator(new File("config/mappingCreator.json")));

            CsvReader reader = new CsvReader(true, identifierList);

            Map<Optional<Reject>, List<Fold>> data;
            final TemplateEngineExecutor templateEngineExecutor = new TemplateEngineExecutor(config);
            //reader.splitFile(new File("test.csv"), new File("testFolder"), 10000, identifierList);
            reader.readFolderParallel(new File(config.getSplittedTempFolder())) // Read lines to -> List<List<String>>
                    .flatMap(Data::foldStream) // FilesRows -> Rows -> Folds
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
                                //return Optional.of(xmlFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //return Optional.<File>empty();
                    });


            //System.out.println(Arrays.stream(Package.getPackages()).filter(aPackage -> aPackage.getName().contains("validator")).collect(Collectors.toList()));

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
}
