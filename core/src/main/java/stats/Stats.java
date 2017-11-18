package stats;

import config.Config;
import cyclops.collections.mutable.MapX;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Stats {

    private static Instant instantStart;

    private static AtomicInteger numberFilesEntry = new AtomicInteger(0);
    private static AtomicInteger numberSplittedFiles = new AtomicInteger(0);

    private static AtomicInteger numberPliEntry = new AtomicInteger(0);
    private static AtomicLong numberDocumentEntry = new AtomicLong(0L);

    private static AtomicInteger numberFileExit = new AtomicInteger(0);
    private static AtomicInteger numberPliExit = new AtomicInteger(0);
    private static AtomicLong numberDocumentExit = new AtomicLong(0L);

    private static AtomicInteger numberPliNotValid = new AtomicInteger(0);
    private static AtomicInteger numberDocumentNotValid = new AtomicInteger(0);

    private static Map<String, AtomicInteger> rejectCounter = new ConcurrentHashMap<>();

    public static void init() {
        instantStart = Instant.now();
        numberFilesEntry = new AtomicInteger(0);
        numberSplittedFiles = new AtomicInteger(0);
        numberPliEntry = new AtomicInteger(0);
        numberDocumentEntry = new AtomicLong(0L);
        numberFileExit = new AtomicInteger(0);
        numberPliExit = new AtomicInteger(0);
        numberDocumentExit = new AtomicLong(0L);
        numberPliNotValid = new AtomicInteger(0);
        numberDocumentNotValid = new AtomicInteger(0);
        rejectCounter = new ConcurrentHashMap<>();
    }

    public static Map<String, AtomicInteger> getRejectCounter() {
        return rejectCounter;
    }

    public static Instant getInstantStart() {
        return instantStart;
    }

    public static void addNumberFilesEntry(final int value) {
        numberFilesEntry.set(numberFilesEntry.get() + value);
    }

    public static void addNumberSplittedFiles(final int value) {
        numberSplittedFiles.getAndAdd(value);
    }

    public static void addNumberPliEntry(final int numberPliEntry) {
        Stats.numberPliEntry.addAndGet(numberPliEntry);
    }

    public static void addNumberDocumentEntry(final int numberDocumentEntry) {
        Stats.numberDocumentEntry.getAndAdd(numberDocumentEntry);
    }

    public static void addNumberFileExit(final int numberFileExit) {
        Stats.numberFileExit.getAndAdd(numberFileExit);
    }

    public static void addNumberPliExit(final int numberPliExit) {
        Stats.numberPliExit.getAndAdd(numberPliExit);
    }

    public static void addNumberDocumentExit(final int numberDocumentExit) {
        Stats.numberDocumentExit.getAndAdd(numberDocumentExit);
    }

    public static void addNumberPliNotValid(final int numberPliNotValid) {
        Stats.numberPliNotValid.getAndAdd(numberPliNotValid);
    }

    public static void addNumberDocumentNotValid(final int numberDocumentNotValid) {
        Stats.numberDocumentNotValid.getAndAdd(numberDocumentNotValid);
    }

    public static AtomicInteger getNumberSplittedFiles() {
        return numberSplittedFiles;
    }
    /**
     * @return the numberFilesEntry
     */
    public static AtomicInteger getNumberFilesEntry() {
        return numberFilesEntry;
    }

    /**
     * @return the numberPliEntry
     */
    public static AtomicInteger getNumberPliEntry() {
        return numberPliEntry;
    }

    /**
     * @return the numberDocumentEntry
     */
    public static AtomicLong getNumberDocumentEntry() {
        return numberDocumentEntry;
    }

    /**
     * @return the numberFileExit
     */
    public static AtomicInteger getNumberFileExit() {
        return numberFileExit;
    }

    /**
     * @return the numberPliExit
     */
    public static AtomicInteger getNumberPliExit() {
        return numberPliExit;
    }

    /**
     * @return the numberDocumentExit
     */
    public static AtomicLong getNumberDocumentExit() {
        return numberDocumentExit;
    }

    /**
     * @return the numberPliNotInExit
     */
    public static AtomicInteger getNumberPliNotValid() {
        return numberPliNotValid;
    }

    /**
     * @return the numberDocumentIgnored
     */
    public static AtomicInteger getNumberDocumentNotValid() {
        return numberDocumentNotValid;
    }

    /**
     * @return the numberFileTreatedPerSeconds
     */
    public static float getNumberFileTreatedPerSeconds() {
        return (numberDocumentNotValid.get() + numberDocumentExit.get()) / (float) (Duration.between(instantStart, Instant.now()).toMillis() / 1000.0);
    }

    private static String getResumeErrors() {
        return MapX.fromMap(getRejectCounter()).stream()
                .map(tupleErrorCounter -> "\t\t"+tupleErrorCounter.v2 + " - " +tupleErrorCounter.v1)
                .join("\n");
    }

    public static String resume() {
        return "======================== RESUME ========================"
                + "\n\tExGED version: "
                + (Stats.class.getPackage().getImplementationVersion() == null ? "dev" : Stats.class.getPackage().getImplementationVersion())
                + "\n\tNom de la migration: "
                + Config.getMainConfig().getMigration()
                + "\n\tNom du traitement: "
                + Config.getMainConfig().getIdTreatment()
                + "\n\tMode: "
                + Config.getMainConfig().getMode()
                + "\n\tHeure d'execution: "
                + Date.from(Instant.now())
                + "\n\tTemps d'execution: "
                + (Duration.between(instantStart, Instant.now()).toMillis() / 1000.0)
                + "\n\tNombre de documents traités par secondes: "
                + getNumberFileTreatedPerSeconds()
                + "\n=================== CHEMINS ========================"
                + "\n\tDossier d'entrée: "
                + Config.getMainConfig().getInputFolder()
                + "\n\tDossier de sortie: "
                + Config.getMainConfig().getOutputFolder()
                + "\n\tRapports et Logs: "
                + Config.getMainConfig().getResumeFolder()
                + "\n\tDossier temporaire: "
                + Config.getMainConfig().getTempFolder()
                + "\n\tDossier de configuration: "
                + Config.getMainConfig().getConfigFolder()
                + "\n=================== ENTREE ========================"
                + "\n\tNombre de fichiers d'entrée traités: "
                + numberFilesEntry.get()
                + "\n\tNombre de plis en entrée: "
                + numberPliEntry.get()
                + "\n\tNombre de documents en entrée: "
                + numberDocumentEntry.get()
                + "\n===================== SORTIE VALIDE ===================="
                + "\n\tNombre de fichiers de sortie générés: "
                + numberFileExit.get()
                + "\n\tNombre de plis en sortie: "
                + numberPliExit.get()
                + "\n\tNombre de documents en sortie: "
                + numberDocumentExit.get()
                + "\n=================== SORTIE NON VALIDE =================="
                + "\n\tNombre de plis non valide "
                + numberPliNotValid.get()
                + "\n\tNombre de documents non valide: "
                + numberDocumentNotValid.get()
                + "\n\tErreurs: \n"
                + getResumeErrors()
                + "\n===================== SORTIE TOTAL ====================="
                + "\n\tNombre de plis: "
                + (numberPliNotValid.get() + numberPliExit.get())
                + "\n\tNombre de documents: "
                + (numberDocumentNotValid.get() + numberDocumentExit.get());
    }
}
