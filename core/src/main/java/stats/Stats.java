package stats;

import java.time.Duration;
import java.time.Instant;

public class Stats {

    private final Instant instantStart;

    private int numberFilesEntry = 0;

    private int numberPliEntry = 0;
    private int numberDocumentEntry = 0;

    private int numberFileExit = 0;
    private int numberPliExit = 0;
    private int numberDocumentExit = 0;

    private int numberPliNotValid = 0;
    private int numberDocumentNotValid = 0;

    public Stats() {
        this.instantStart = Instant.now();
    }

    public Instant getInstantStart() {
        return instantStart;
    }

    public final synchronized void addNumberFilesEntry(final int value) {
        numberFilesEntry = numberFilesEntry + value;
    }

    public final synchronized void addNumberPliEntry(final int numberPliEntry) {
        this.numberPliEntry = this.numberPliEntry + numberPliEntry;
    }

    public final synchronized void addNumberDocumentEntry(final int numberDocumentEntry) {
        this.numberDocumentEntry = this.numberDocumentEntry + numberDocumentEntry;
    }

    public final synchronized void addNumberFileExit(final int numberFileExit) {
        this.numberFileExit = this.numberFileExit + numberFileExit;
    }

    public final synchronized void addNumberPliExit(final int numberPliExit) {
        this.numberPliExit = this.numberPliExit + numberPliExit;
    }

    public final synchronized void addNumberDocumentExit(final int numberDocumentExit) {
        this.numberDocumentExit = this.numberDocumentExit + numberDocumentExit;
    }

    public final synchronized void addNumberPliNotValid(final int numberPliNotValid) {
        this.numberPliNotValid = this.numberPliNotValid + numberPliNotValid;
    }

    public final synchronized void addNumberDocumentNotValid(final int numberDocumentNotValid) {
        this.numberDocumentNotValid = this.numberDocumentNotValid + numberDocumentNotValid;
    }

    /**
     * @return the numberFilesEntry
     */
    public final synchronized int getNumberFilesEntry() {
        return numberFilesEntry;
    }

    /**
     * @return the numberPliEntry
     */
    public final synchronized int getNumberPliEntry() {
        return numberPliEntry;
    }

    /**
     * @return the numberDocumentEntry
     */
    public final synchronized int getNumberDocumentEntry() {
        return numberDocumentEntry;
    }

    /**
     * @return the numberFileExit
     */
    public final synchronized int getNumberFileExit() {
        return numberFileExit;
    }

    /**
     * @return the numberPliExit
     */
    public final synchronized int getNumberPliExit() {
        return numberPliExit;
    }

    /**
     * @return the numberDocumentExit
     */
    public final synchronized int getNumberDocumentExit() {
        return numberDocumentExit;
    }

    /**
     * @return the numberPliNotInExit
     */
    public final synchronized int getNumberPliNotValid() {
        return numberPliNotValid;
    }

    /**
     * @return the numberDocumentIgnored
     */
    public final synchronized int getNumberDocumentNotValid() {
        return numberDocumentNotValid;
    }

    /**
     * @return the numberFileTreatedPerSeconds
     */
    public final synchronized float getNumberFileTreatedPerSeconds() {
        return getNumberDocumentEntry() / (Duration.between(this.instantStart, Instant.now()).getNano() / 1000000000f);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SORTIE: "
                + "\n\tTemps d'execution: "
                + (Duration.between(this.instantStart, Instant.now()).getNano() / 1000000000f)
                + "\n\tNombre de documents traités par secondes: "
                + getNumberFileTreatedPerSeconds()
                + "\n======================== ENTREE ========================"
                + "\n\tNombre de fichiers d'entrée traités: "
                + numberFilesEntry
                + "\n\tNombre de plis en entrée: "
                + numberPliEntry
                + "\n\tNombre de documents en entrée: "
                + numberDocumentEntry
                + "\n===================== SORTIE VALIDE ===================="
                + "\n\tNombre de fichiers de sortie générés: "
                + numberFileExit
                + "\n\tNombre de plis en sortie: "
                + numberPliExit
                + "\n\tNombre de documents en sortie: "
                + numberDocumentExit
                + "\n=================== SORTIE NON VALIDE =================="
                + "\n\tNombre de plis non valide "
                + numberPliNotValid
                + "\n\tNombre de documents non valide: "
                + numberDocumentNotValid;
    }
}
