package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a request to import an address book from a specified file.
 */
public class ImportRequest {
    private final Path filePath;

    public ImportRequest(Path filePath) {
        requireAllNonNull(filePath);
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return this.filePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ImportRequest otherImportRequest)) {
            return false;
        }

        return filePath.equals(otherImportRequest.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath);
    }
}
