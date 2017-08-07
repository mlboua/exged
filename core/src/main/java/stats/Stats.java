package stats;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Stats {

    private final Instant instantStart;

    private AtomicInteger numberFilesEntry = new AtomicInteger(0);

    private AtomicInteger numberPliEntry = new AtomicInteger(0);
    private AtomicLong numberDocumentEntry = new AtomicLong(0L);

    private AtomicInteger numberFileExit = new AtomicInteger(0);
    private AtomicInteger numberPliExit = new AtomicInteger(0);
    private AtomicLong numberDocumentExit = new AtomicLong(0L);

    private AtomicInteger numberPliNotValid = new AtomicInteger(0);
    private AtomicInteger numberDocumentNotValid = new AtomicInteger(0);

    public Stats() {
        this.instantStart = Instant.now();
    }

    public Instant getInstantStart() {
        return instantStart;
    }

    public final void addNumberFilesEntry(final int value) {
        numberFilesEntry.set(numberFilesEntry.get() + value);
    }

    public final void addNumberPliEntry(final int numberPliEntry) {
        this.numberPliEntry.addAndGet(numberPliEntry);
    }

    public final void addNumberDocumentEntry(final int numberDocumentEntry) {
        this.numberDocumentEntry.addAndGet(numberDocumentEntry);
    }

    public final void addNumberFileExit(final int numberFileExit) {
        this.numberFileExit.addAndGet(numberFileExit);
    }

    public final void addNumberPliExit(final int numberPliExit) {
        this.numberPliExit.addAndGet(numberPliExit);
    }

    public final void addNumberDocumentExit(final int numberDocumentExit) {
        this.numberDocumentExit.addAndGet(numberDocumentExit);
    }

    public final void addNumberPliNotValid(final int numberPliNotValid) {
        this.numberPliNotValid.addAndGet(numberPliNotValid);
    }

    public final void addNumberDocumentNotValid(final int numberDocumentNotValid) {
        this.numberDocumentNotValid.addAndGet(numberDocumentNotValid);
    }

    /**
     * @return the numberFilesEntry
     */
    public final AtomicInteger getNumberFilesEntry() {
        return numberFilesEntry;
    }

    /**
     * @return the numberPliEntry
     */
    public final AtomicInteger getNumberPliEntry() {
        return numberPliEntry;
    }

    /**
     * @return the numberDocumentEntry
     */
    public final AtomicLong getNumberDocumentEntry() {
        return numberDocumentEntry;
    }

    /**
     * @return the numberFileExit
     */
    public final AtomicInteger getNumberFileExit() {
        return numberFileExit;
    }

    /**
     * @return the numberPliExit
     */
    public final AtomicInteger getNumberPliExit() {
        return numberPliExit;
    }

    /**
     * @return the numberDocumentExit
     */
    public final AtomicLong getNumberDocumentExit() {
        return numberDocumentExit;
    }

    /**
     * @return the numberPliNotInExit
     */
    public final AtomicInteger getNumberPliNotValid() {
        return numberPliNotValid;
    }

    /**
     * @return the numberDocumentIgnored
     */
    public final AtomicInteger getNumberDocumentNotValid() {
        return numberDocumentNotValid;
    }

    /**
     * @return the numberFileTreatedPerSeconds
     */
    public final float getNumberFileTreatedPerSeconds() {
        return getNumberDocumentEntry().get() / (float) (Duration.between(this.instantStart, Instant.now()).toMillis() / 1000.0);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "======================== RESUME ========================"
                + "\n\tTemps d'execution: "
                + (Duration.between(this.instantStart, Instant.now()).toMillis() / 1000.0)
                + "\n\tNombre de documents traités par secondes: "
                + getNumberFileTreatedPerSeconds()
                + "\n======================== ENTREE ========================"
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
                + numberDocumentNotValid.get();
    }
}
