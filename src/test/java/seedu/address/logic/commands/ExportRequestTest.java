package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;

public class ExportRequestTest {

    @Test
    public void constructor_storesAddressBookAndFilePath() {
        AddressBook addressBook = new AddressBook();
        Path filePath = Path.of("data", "exports", "contacts.json");

        ExportRequest exportRequest = new ExportRequest(addressBook, filePath);

        assertSame(addressBook, exportRequest.getAddressBook());
        assertSame(filePath, exportRequest.getFilePath());
    }
}
