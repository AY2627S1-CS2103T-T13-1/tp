package seedu.address.logic.commands;

/**
 * Represents the result of merging contacts from an imported address book.
 *
 * The summary records how many contacts were imported successfully and how
 * many were skipped because they duplicated existing contacts.
 */
public class ImportSummary {
    private final int importedCount;
    private final int skippedCount;

    /**
     * Constructs an {@code ImportSummary} with the specified contact counts.
     *
     * @param importedCount the number of contacts added to the address book
     * @param skippedCount the number of duplicate contacts skipped
     */
    public ImportSummary(int importedCount, int skippedCount) {
        this.importedCount = importedCount;
        this.skippedCount = skippedCount;
    }

    public int getImportedCount() {
        return this.importedCount;
    }

    public int getSkippedCount() {
        return this.skippedCount;
    }

    public String getMessage() {
        return String.format("Imported %d contact(s). Skipped %d duplicate(s).",
                importedCount,
                skippedCount);
    }
}
