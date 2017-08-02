package config.json.mapping.mainConfig;

import java.io.File;

public class Config {

    // Global
    private String idTreatement;
    private String migration;
    private String mode;
    private int numberOfFoldPerArchive;

    // Sort
    private String foldSort;

    // Folders config
    private String inputFolder;

    private String tempFolder;
    private String splittedTempFolder;
    private String pivotTempFolder;
    private String xmlTempFolder;

    private String outputFolder;

    private String pdfPath;

    private String resumeFolder;
    private String logFolder;
    private String reportsFolder;

    private String configFolder;
    private String templateFolder;

    // Step 1 - Splitter
    private int splitterNumberOfLinePerFile; // 100 000 Offer the best performance, More than 200 000 use too much ram
    // and the processing will be much longer

    // Step 2 - Extraction
    // Files in "configFolder"
    private String headersFile;
    private String sStoRNIFile;
    private String oldToNewModelFile;

    // Step 3 - Traduction
    // Files in "configFolder"
    private String configMetierFile; // ExGED Only

    /**
     *
     */
    public Config() {

    }

    public Config(final String idTreatement, final String migration, final String mode,
                  final int numberOfFoldPerArchive, final String foldSort, final String inputFolder, final String tempFolder,
                  final String pdfPath, final String resumeFolder, final String configFolder,
                  final int splitterNumberOfLinePerFile, final String headersFile, final String sStoRNIFile,
                  final String oldToNewModelFile, final String configMetierFile) {
        this.idTreatement = idTreatement;
        this.migration = migration;
        this.mode = mode;
        this.numberOfFoldPerArchive = numberOfFoldPerArchive;
        this.foldSort = foldSort;
        this.inputFolder = inputFolder;

        this.tempFolder = tempFolder;

        this.pdfPath = pdfPath;
        this.resumeFolder = resumeFolder;

        this.configFolder = configFolder;

        this.splitterNumberOfLinePerFile = splitterNumberOfLinePerFile;
        this.headersFile = headersFile;
        this.sStoRNIFile = sStoRNIFile;
        this.oldToNewModelFile = oldToNewModelFile;
        this.configMetierFile = configMetierFile;
    }

    public void init() {
        splittedTempFolder = tempFolder + File.separator + idTreatement + File.separator + "splitted";
        pivotTempFolder = tempFolder + File.separator + idTreatement + File.separator + "pivot";
        xmlTempFolder = tempFolder + File.separator + idTreatement + File.separator + "xml";

        logFolder = resumeFolder + File.separator + idTreatement + File.separator + "logs";
        reportsFolder = resumeFolder + File.separator + idTreatement + File.separator + "reports";

        templateFolder = configFolder + File.separator + "templates";

        inputFolder = inputFolder + File.separator + idTreatement;
        outputFolder = outputFolder + File.separator + idTreatement;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public String getSplittedTempFolder() {
        return splittedTempFolder;
    }

    public String getPivotTempFolder() {
        return pivotTempFolder;
    }

    public String getXmlTempFolder() {
        return xmlTempFolder;
    }

    public String getIdTreatement() {
        return idTreatement;
    }

    public void setIdTreatement(final String idTreatement) {
        this.idTreatement = idTreatement;
    }

    public String getResumeFolder() {
        return resumeFolder;
    }

    public void setResumeFolder(final String resumeFolder) {
        this.resumeFolder = resumeFolder;
    }

    public String getsStoRNIFile() {
        return sStoRNIFile;
    }

    public void setsStoRNIFile(final String sStoRNIFile) {
        this.sStoRNIFile = sStoRNIFile;
    }

    public void setIdTraitement(final String idTraitement) {
        idTreatement = idTraitement;
    }

    public String getMigration() {
        return migration;
    }

    public void setMigration(final String migration) {
        this.migration = migration;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public int getNumberOfFoldPerArchive() {
        return numberOfFoldPerArchive;
    }

    public void setNumberOfFoldPerArchive(final int numberOfFoldPerArchive) {
        this.numberOfFoldPerArchive = numberOfFoldPerArchive;
    }

    public String getFoldSort() {
        return foldSort;
    }

    public void setFoldSort(final String foldSort) {
        this.foldSort = foldSort;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(final String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(final String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(final String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(final String logFolder) {
        this.logFolder = logFolder;
    }

    public String getReportsFolder() {
        return reportsFolder;
    }

    public void setReportsFolder(final String reportsFolder) {
        this.reportsFolder = reportsFolder;
    }

    public String getConfigFolder() {
        return configFolder;
    }

    public void setConfigFolder(final String configFolder) {
        this.configFolder = configFolder;
    }

    public int getSplitterNumberOfLinePerFile() {
        return splitterNumberOfLinePerFile;
    }

    public void setSplitterNumberOfLinePerFile(final int splitterNumberOfLinePerFile) {
        this.splitterNumberOfLinePerFile = splitterNumberOfLinePerFile;
    }

    public String getHeadersFile() {
        return headersFile;
    }

    public void setHeadersFile(final String headersFile) {
        this.headersFile = headersFile;
    }

    public String getOldToNewModelFile() {
        return oldToNewModelFile;
    }

    public void setOldToNewModelFile(final String oldToNewModelFile) {
        this.oldToNewModelFile = oldToNewModelFile;
    }

    public String getConfigMetierFile() {
        return configMetierFile;
    }

    public void setConfigMetierFile(final String configMetierFile) {
        this.configMetierFile = configMetierFile;
    }

    @Override
    public String toString() {
        return "Config [idTreatement="
                + idTreatement
                + ", migration="
                + migration
                + ", mode="
                + mode
                + ", numberOfFoldPerArchive="
                + numberOfFoldPerArchive
                + ", foldSort="
                + foldSort
                + ", inputFolder="
                + inputFolder
                + ", tempFolder="
                + tempFolder
                + ", splittedTempFolder="
                + splittedTempFolder
                + ", pivotTempFolder="
                + pivotTempFolder
                + ", xmlTempFolder="
                + xmlTempFolder
                + ", outputFolder="
                + outputFolder
                + ", pdfPath="
                + pdfPath
                + ", resumeFolder="
                + resumeFolder
                + ", logFolder="
                + logFolder
                + ", reportsFolder="
                + reportsFolder
                + ", configFolder="
                + configFolder
                + ", templateFolder="
                + templateFolder
                + ", splitterNumberOfLinePerFile="
                + splitterNumberOfLinePerFile
                + ", headersFile="
                + headersFile
                + ", sStoRNIFile="
                + sStoRNIFile
                + ", oldToNewModelFile="
                + oldToNewModelFile
                + ", configMetierFile="
                + configMetierFile
                + "]";
    }
}
