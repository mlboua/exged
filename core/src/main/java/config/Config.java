package config;

import java.util.List;

import config.mapping.creators.CreatorConfig;
import config.mapping.delivery.DeliveryConfig;
import config.mapping.mainconfig.MainConfig;
import config.mapping.reject.RejectConfig;
import config.mapping.reports.ReportsConfig;
import config.mapping.validations.ValidatorsConfig;
import identifier.Identifier;

public class Config {

    private static MainConfig mainConfig;
    private static ValidatorsConfig validators;

    private static List<Identifier> identifiers;
    private static List<CreatorConfig> creators;
    private static List<RejectConfig> rejects;
    private static List<ReportsConfig> reports;
    private static List<DeliveryConfig> delivery;

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

    public static void setMainConfig(final MainConfig mainConfig) {
        Config.mainConfig = mainConfig;
    }

    public static void setValidators(final ValidatorsConfig validators) {
        Config.validators = validators;
    }

    public static void setIdentifiers(final List<Identifier> identifiers) {
        Config.identifiers = identifiers;
    }

    public static void setCreators(final List<CreatorConfig> creators) {
        Config.creators = creators;
    }

    public static void setRejects(final List<RejectConfig> rejects) {
        Config.rejects = rejects;
    }

    public static void setReports(final List<ReportsConfig> reports) {
        Config.reports = reports;
    }

    public static void setDelivery(final List<DeliveryConfig> delivery) {
        Config.delivery = delivery;
    }
}
