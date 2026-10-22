package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Objects;

import seedu.address.model.ReadOnlyAddressBook;

/**
 * Represents a request to export an address book to a specified file.
 */
public class ExportRequest {
    private final ReadOnlyAddressBook addressBook;
    private final Path filePath;

    /**
     * Constructs an {@code ExportRequest} with the specified address book and file path.
     *
     * @param addressBook the address book to export
     * @param filePath the path of the target file to export to
     */
    public ExportRequest(ReadOnlyAddressBook addressBook, Path filePath) {
        requireAllNonNull(addressBook, filePath);
        this.addressBook = addressBook;
        this.filePath = filePath;
    }

    /**
     * Returns the address book to be exported.
     *
     * @return the address book associated with this request
     */
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /**
     * Returns the path of the file to export to.
     *
     * @return the export file path
     */
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ExportRequest otherExportRequest)) {
            return false;
        }

        return addressBook.equals(otherExportRequest.addressBook)
                && filePath.equals(otherExportRequest.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressBook, filePath);
    }
}
