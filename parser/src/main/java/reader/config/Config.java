package reader.config;

import exception.ExgedParserException;
import identifier.Identifier;
import reader.config.mapping.creators.CreatorConfig;
import reader.config.mapping.delivery.DeliveryConfig;
import reader.config.mapping.mainconfig.MainConfig;
import reader.config.mapping.reject.RejectConfig;
import reader.config.mapping.reports.ReportsConfig;
import reader.config.mapping.validations.ValidatorsConfig;

import java.io.File;
import java.util.List;

public class Config {

    private static MainConfig mainConfig;
    private static ValidatorsConfig validators;

    private static List<Identifier> identifiers;
    private static List<CreatorConfig> creators;
    private static List<RejectConfig> rejects;
    private static List<ReportsConfig> reports;
    private static List<DeliveryConfig> delivery;

    private Config() {
    }

    public static void initConfig(File mainConfigFile) throws ExgedParserException {
        mainConfig = ConfigReader.readMainConfig(mainConfigFile);
        validators = ConfigReader.readValidatorsConfig();

        identifiers = ConfigReader.readIdentifiersConfig();
        creators = ConfigReader.readCreatorsConfig();
        rejects = ConfigReader.readRejectsConfig();
        reports = ConfigReader.readReportsConfig();
        delivery = ConfigReader.readDeliveryConfig();
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    public static ValidatorsConfig getValidators() {
        return validators;
    }

    public static List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public static List<CreatorConfig> getCreators() {
        return creators;
    }

    public static List<RejectConfig> getRejects() {
        return rejects;
    }

    public static List<ReportsConfig> getReports() {
        return reports;
    }

    public static List<DeliveryConfig> getDelivery() {
        return delivery;
    }
}
