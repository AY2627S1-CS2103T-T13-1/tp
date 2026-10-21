package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

public class ExportCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noCriteria_exportsAllContacts() throws Exception {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Set.of());
        ExportCommand command = new ExportCommand("samplefile", predicate);

        CommandResult result = command.execute(model);

        assertEquals("Contact(s) exported to samplefile.json", result.getFeedbackToUser());
        assertTrue(result.getExportRequest().isPresent());
        ExportRequest exportRequest = result.getExportRequest().orElseThrow();
        assertEquals(Path.of("data", "exports", "samplefile.json"), exportRequest.getFilePath());
        assertEquals(getTypicalAddressBook(), exportRequest.getAddressBook());
    }

    @Test
    public void execute_withCriteria_exportsMatchingContacts() throws Exception {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Meier"), Optional.empty(), Optional.empty(), Optional.empty(), Set.of());
        ExportCommand command = new ExportCommand("meier-contacts", predicate);

        CommandResult result = command.execute(model);

        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.setPersons(List.of(BENSON, DANIEL));
        assertEquals("Contact(s) exported to meier-contacts.json", result.getFeedbackToUser());
        ExportRequest exportRequest = result.getExportRequest().orElseThrow();
        assertEquals(expectedAddressBook, exportRequest.getAddressBook());
    }

    @Test
    public void execute_noMatchingContacts_returnsFailureResult() throws Exception {
        ExportCommand command = new ExportCommand("empty", person -> false);

        CommandResult result = command.execute(model);

        assertEquals(ExportCommand.EXPORT_FAILED_NONE_MATCH, result.getFeedbackToUser());
        assertFalse(result.getExportRequest().isPresent());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ExportCommand command = new ExportCommand("samplefile", person -> true);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
