package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ImportCommandTest {

    private final Model model = new ModelManager(
            getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);

    @Test
    public void execute_returnsImportRequest() throws Exception {
        Path filePath = Path.of("data", "exports", "contacts.json");
        ImportCommand command = new ImportCommand(filePath);

        CommandResult result = command.execute(model);

        assertEquals(ImportCommand.MESSAGE_IMPORT_REQUESTED, result.getFeedbackToUser());
        assertTrue(result.getImportRequest().isPresent());
        assertEquals(filePath, result.getImportRequest().orElseThrow().getFilePath());
        assertFalse(result.getExportRequest().isPresent());
    }

    @Test
    public void execute_doesNotModifyModel() throws Exception {
        Model expectedModel = new ModelManager(
                getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        ImportCommand command = new ImportCommand(Path.of("contacts.json"));

        command.execute(model);

        assertEquals(expectedModel.getAddressBook(), model.getAddressBook());
    }

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ImportCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ImportCommand command = new ImportCommand(Path.of("contacts.json"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_sameFilePath_returnsTrue() {
        Path filePath = Path.of("contacts.json");

        ImportCommand firstCommand = new ImportCommand(filePath);
        ImportCommand secondCommand = new ImportCommand(filePath);

        assertEquals(firstCommand, secondCommand);
    }

    @Test
    public void equals_differentFilePath_returnsFalse() {
        ImportCommand firstCommand = new ImportCommand(Path.of("contacts.json"));
        ImportCommand secondCommand = new ImportCommand(Path.of("other-contacts.json"));

        assertFalse(firstCommand.equals(secondCommand));
    }
}
