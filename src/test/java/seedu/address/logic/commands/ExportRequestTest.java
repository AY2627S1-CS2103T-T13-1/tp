package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;

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

    @Test
    public void constructor_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new ExportRequest(null, Path.of("contacts.json")));
    }

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new ExportRequest(new AddressBook(), null));
    }
}
