package config.mapping.mainconfig;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MainConfig {

    // Global
    private final String idTreatment;
    private final String migration;
    private final String mode;

    // Path config
    private final String configFolder;

    private final String inputFolder;
    private final String tempFolder;
    private final String splittedTempFolder;
    private final String outputTempFolder;
    private final String resumeFolder;
    private final String outputFolder;
    private final String externFilesPath;

    private final String logFolder;
    private final String reportsFolder;

    private final String templateFolder;

    private final int tempFilesNumberOfLines;

    private final String xsdValidator;

    private final String csvDelimiter;

    @JsonCreator
    public MainConfig(@JsonProperty("idTreatment") final String idTreatment,
            @JsonProperty("migration") final String migration, @JsonProperty("mode") final String mode,
            @JsonProperty("tempFilesNumberOfLines") final int tempFilesNumberOfLines,
            @JsonProperty("inputFolder") final String inputFolder, @JsonProperty("tempFolder") final String tempFolder,
            @JsonProperty("externFilesPath") final String externFilesPath,
            @JsonProperty("resumeFolder") final String resumeFolder,
            @JsonProperty("configFolder") final String configFolder,
            @JsonProperty("xsdValidator") final String xsdValidator,
            @JsonProperty("csvDelimiter") final String csvDelimiter,
            @JsonProperty("outputFolder") final String outputFolder) {
        this.idTreatment = idTreatment;
        this.migration = migration;
        this.mode = mode;
        this.tempFilesNumberOfLines = tempFilesNumberOfLines;
        this.tempFolder = tempFolder;
        this.externFilesPath = externFilesPath;
        this.resumeFolder = resumeFolder;
        this.configFolder = configFolder;
        this.xsdValidator = xsdValidator;
        this.csvDelimiter = csvDelimiter;

        this.inputFolder = inputFolder + File.separator + idTreatment;
        splittedTempFolder = tempFolder + File.separator + idTreatment + File.separator + "splitted";
        outputTempFolder = tempFolder + File.separator + idTreatment + File.separator + "output";
        logFolder = resumeFolder + File.separator + idTreatment + File.separator + "logs";
        reportsFolder = resumeFolder + File.separator + idTreatment + File.separator + "reports";
        templateFolder = configFolder + File.separator + "templates";
        this.outputFolder = outputFolder + File.separator + idTreatment;
    }

    public String getIdTreatment() {
        return idTreatment;
    }

    public String getMigration() {
        return migration;
    }

    public String getMode() {
        return mode;
    }

    public String getConfigFolder() {
        return configFolder;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public String getSplittedTempFolder() {
        return splittedTempFolder;
    }

    public String getOutputTempFolder() {
        return outputTempFolder;
    }

    public String getResumeFolder() {
        return resumeFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getExternFilesPath() {
        return externFilesPath;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public String getReportsFolder() {
        return reportsFolder;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public int getTempFilesNumberOfLines() {
        return tempFilesNumberOfLines;
    }

    public String getXsdValidator() {
        return xsdValidator;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    @Override
    public String toString() {
        return "MainConfig{" + "idTreatment='" + idTreatment + '\'' + ", migration='" + migration + '\'' + ", mode='"
                + mode + '\'' + ", configFolder='" + configFolder + '\'' + ", inputFolder='" + inputFolder + '\''
                + ", tempFolder='" + tempFolder + '\'' + ", splittedTempFolder='" + splittedTempFolder + '\''
                + ", outputTempFolder='" + outputTempFolder + '\'' + ", resumeFolder='" + resumeFolder + '\''
                + ", outputFolder='" + outputFolder + '\'' + ", externFilesPath='" + externFilesPath + '\''
                + ", logFolder='" + logFolder + '\'' + ", reportsFolder='" + reportsFolder + '\'' + ", templateFolder='"
                + templateFolder + '\'' + ", tempFilesNumberOfLines=" + tempFilesNumberOfLines + ", xsdValidator = '"
                + xsdValidator + "', csvDelimiter = '" + csvDelimiter + "'" + '}';
    }
}
