package initialisation;

import config.json.mapping.mainConfig.Config;

import java.io.File;

public class InitFolders {

    private InitFolders() {
    }

    public static void init(final Config config) {
        createDirectory(config.getConfigFolder());
        createDirectory(config.getInputFolder());
        createDirectory(config.getLogFolder());
        createDirectory(config.getOutputFolder());
        createDirectory(config.getSplittedTempFolder());
        createDirectory(config.getPivotTempFolder());
        createDirectory(config.getReportsFolder());
        createDirectory(config.getResumeFolder());
        createDirectory(config.getTempFolder());
        createDirectory(config.getTemplateFolder());
        createDirectory(config.getXmlTempFolder());
    }

    private static void createDirectory(final String path) {
        new File(path).mkdirs();
    }
}
