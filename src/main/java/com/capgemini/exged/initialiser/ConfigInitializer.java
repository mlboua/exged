package com.capgemini.exged.initialiser;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.beust.jcommander.JCommander;
import com.capgemini.exged.exception.ExgedMainException;

import config.Config;
import config.ConfigReader;
import exception.ExgedParserException;
import initialisation.Args;
import initialisation.InitFolders;

public class ConfigInitializer {

    private static final List<String> defaultPathConfig = Arrays.asList("config.yaml", "config.json",
            "config/config.yaml", "config/config.json");

    private ConfigInitializer() {
    }

    private static Args readArguments(final String... args) {
        final Args arguments = new Args();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        return arguments;
    }

    /**
     * Init the main configuration from file (config.yam)
     * 
     * @param arguments
     * @return
     */
    private static Optional<File> getMainConfigFile(final Args arguments) {
        return arguments.getConfigPath() != null ? Optional.of(new File(arguments.getConfigPath()))
                : defaultPathConfig.stream().filter(path -> new File(path).exists()).findFirst().map(File::new);
    }

    /**
     * Initialize all the project setting values
     * 
     * @param args
     * @throws ExgedMainException
     */
    public static void initConfig(final String... args) throws ExgedMainException {
        final Optional<File> mainConfigFile = getMainConfigFile(readArguments(args));
        if (mainConfigFile.isPresent()) {
            try {
                Config.setMainConfig(ConfigReader.readMainConfig(mainConfigFile.get()));
                Config.setValidators(ConfigReader.readValidatorsConfig());
                Config.setIdentifiers(ConfigReader.readIdentifiersConfig());
                Config.setCreators(ConfigReader.readCreatorsConfig());
                Config.setRejects(ConfigReader.readRejectsConfig());
                Config.setReports(ConfigReader.readReportsConfig());
                Config.setDelivery(ConfigReader.readDeliveryConfig());
                InitFolders.init(Config.getMainConfig());
            } catch (final ExgedParserException e) {
                throw new ExgedMainException("Problème lors de l'initialisation des fichiers de configuration: " + e);
            }
        } else {
            throw new ExgedMainException(
                    "Fichier de configuration introuvable, veuillez le renseigner en argument avec \"-c\"");
        }
    }
}
