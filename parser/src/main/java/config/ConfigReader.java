package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import config.mapping.creators.CreatorConfig;
import config.mapping.delivery.DeliveryConfig;
import config.mapping.mainconfig.MainConfig;
import config.mapping.reject.RejectConfig;
import config.mapping.reports.ReportsConfig;
import config.mapping.validations.ValidatorsConfig;
import exception.ExgedParserException;
import identifier.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ConfigReader {

    private static final Map<String, ObjectMapper> objectMapperMap;

    static {
        objectMapperMap = new HashMap<>();
        objectMapperMap.put("json",
                new ObjectMapper()
                        .registerModule(new ParameterNamesModule())
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule()));
        objectMapperMap.put("yaml",
                new ObjectMapper(new YAMLFactory())
                        .registerModule(new ParameterNamesModule())
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule()));
        objectMapperMap.put("yml", objectMapperMap.get("yaml"));
    }

    private ConfigReader() {
    }

    private static Optional<File> getConfigFile(String configFile) {
        return objectMapperMap.keySet().stream()
                .filter(extension ->
                        Paths.get(Config.getMainConfig().getConfigFolder() + File.separator + configFile + "." + extension).toFile().exists()
                )
                .findFirst()
                .map(extension -> new File(Config.getMainConfig().getConfigFolder() + File.separator + configFile + "." + extension));

    }

    public static MainConfig readMainConfig(File file) throws ExgedParserException {
        try {
            return objectMapperMap.get(getFileExtension(file)).readValue(file, MainConfig.class);
        } catch (IOException e) {
            throw new ExgedParserException("Impossible de charger le fichier de configuration principal: " + e);
        }
    }

    private static String getFileExtension(File file) {
        return file.getName().split("\\.")[1];
    }

    public static List<CreatorConfig> readCreatorsConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("creators");
        if (configFile.isPresent()) {
            try {
                return Arrays.asList(objectMapperMap.get(getFileExtension(configFile.get()))
                        .readValue(configFile.get(), CreatorConfig[].class));
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"creators\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"creators\"");
        }
    }

    public static List<RejectConfig> readRejectsConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("rejects");
        if (configFile.isPresent()) {
            try {
                return Arrays.asList(objectMapperMap.get(getFileExtension(configFile.get()))
                        .readValue(configFile.get(), RejectConfig[].class));
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"reject\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"reject\"");
        }
    }

    public static List<ReportsConfig> readReportsConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("reports");
        if (configFile.isPresent()) {
            try {
                return Arrays.asList(objectMapperMap.get(getFileExtension(configFile.get()))
                        .readValue(configFile.get(), ReportsConfig[].class));
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"reports\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"reports\"");
        }
    }

    public static ValidatorsConfig readValidatorsConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("validators");
        if (configFile.isPresent()) {
            try {
                return objectMapperMap.get(getFileExtension(configFile.get()))
                        .readValue(configFile.get(), ValidatorsConfig.class);
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"validators\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"validators\"");
        }
    }

    public static List<Identifier> readIdentifiersConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("identifiers");
        if (configFile.isPresent()) {
            try {
                List<Identifier> identifierUnorderedList = new ArrayList<>(Arrays.asList(
                        objectMapperMap.get(getFileExtension(configFile.get()))
                                .readValue(configFile.get(), Identifier[].class)));
                if (identifierUnorderedList.stream()
                        .filter(identifier -> !identifier.getReplacedBy().isPresent())
                        .count() > 1) {
                    throw new ExgedParserException("Plusieurs identifiants n'ont pas de valeurs de remplacement, il faut qu'un seul identifiant qui ne soit pas remplacé");
                }
                List<Identifier> reversedList = new ArrayList<>();
                Optional<Identifier> lastIdentifier = identifierUnorderedList.stream()
                        .filter(identifier -> !identifier.getReplacedBy().isPresent())
                        .findFirst();
                if (lastIdentifier.isPresent()) {
                    reversedList.add(lastIdentifier.get());
                    identifierUnorderedList.forEach(identifier -> identifierUnorderedList.stream()
                            .filter(identifierSearch ->
                                    identifierSearch.getReplacedBy().isPresent()
                                            && reversedList.get(reversedList.size() - 1).getName().equals(identifierSearch.getReplacedBy().get()))
                            .findFirst()
                            .ifPresent(reversedList::add));
                } else {
                    throw new ExgedParserException("Aucun identifiant sans remplacant trouvé, il faut un identifiant sans remplacant");
                }
                Collections.reverse(reversedList);
                return reversedList;
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"identifiers\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"identifiers\"");
        }
    }

    public static List<DeliveryConfig> readDeliveryConfig() throws ExgedParserException {
        final Optional<File> configFile = getConfigFile("delivery");
        if (configFile.isPresent()) {
            try {
                return Arrays.asList(objectMapperMap.get(getFileExtension(configFile.get()))
                        .readValue(configFile.get(), DeliveryConfig[].class));
            } catch (IOException e) {
                throw new ExgedParserException("Impossible de lire le fichier \"delivery\": " + e);
            }
        } else {
            throw new ExgedParserException("Impossible de trouver le fichier \"delivery\"");
        }
    }
}
