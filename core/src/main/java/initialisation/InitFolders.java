package initialisation;

import reader.config.mapping.mainconfig.MainConfig;

import java.io.File;

public class InitFolders {

    private InitFolders() {
    }

    public static void init(final MainConfig mainConfig) {
        createDirectory(mainConfig.getConfigFolder());
        createDirectory(mainConfig.getInputFolder());
        createDirectory(mainConfig.getLogFolder());
        createDirectory(mainConfig.getOutputFolder());
        createDirectory(mainConfig.getSplittedTempFolder());
        createDirectory(mainConfig.getReportsFolder());
        createDirectory(mainConfig.getResumeFolder());
        createDirectory(mainConfig.getTempFolder());
        createDirectory(mainConfig.getTemplateFolder());
        createDirectory(mainConfig.getOutputTempFolder());
    }

    private static void createDirectory(final String path) {
        new File(path).mkdirs();
    }
}
