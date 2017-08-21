package com.capgemini.exged.initialiser;

import com.beust.jcommander.JCommander;
import com.capgemini.exged.exception.ExgedMainException;
import config.Config;
import exception.ExgedParserException;
import initialisation.Args;
import initialisation.InitFolders;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigInitialiser {

    private static final List<String> defaultPathConfig = Arrays.asList("config.yaml", "config.json",
            "config/config.yaml", "config/config.json");

    private ConfigInitialiser() {
    }

    private static Args readArguments(String... args) {
        final Args arguments = new Args();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        return arguments;
    }

    private static Optional<File> getMainConfigFile(Args arguments) {
        return arguments.getConfigPath() != null ?
                Optional.of(new File(arguments.getConfigPath())) :
                defaultPathConfig.stream().filter(path -> new File(path).exists()).findFirst().map(File::new);
    }

    public static void initConfig(String... args) throws ExgedMainException {
        final Optional<File> mainConfigFile = getMainConfigFile(readArguments(args));
        if (mainConfigFile.isPresent()) {
            try {
                Config.initConfig(mainConfigFile.get());
                InitFolders.init(Config.getMainConfig());
            } catch (ExgedParserException e) {
                throw new ExgedMainException("Problème lors de l'initialisation des fichiers de configuration: " + e);
            }
        } else {
            throw new ExgedMainException("Fichier de configuration introuvable, veuillez le renseigner en argument avec \"-c\"");
        }
    }
}
