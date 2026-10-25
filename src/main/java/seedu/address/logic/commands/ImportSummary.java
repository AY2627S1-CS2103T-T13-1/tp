package seedu.address.logic.commands;

public class ImportSummary {
    private final int importedCount;
    private final int skippedCount;

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
