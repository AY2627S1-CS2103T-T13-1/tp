package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.ExportRequest;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.Filename;

public class ExportCommandParserTest {

    private final ExportCommandParser parser = new ExportCommandParser();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void parse_filenameOnly_exportsAllContacts() throws ParseException {
        ExportCommand command = parser.parse(" -f samplefile");

        CommandResult result = command.execute(model);
        ExportRequest exportRequest = result.getExportRequest().orElseThrow();

        assertEquals("Contact(s) exported to samplefile.json", result.getFeedbackToUser());
        assertEquals(Path.of("data", "exports", "samplefile.json"), exportRequest.getFilePath());
        assertEquals(getTypicalAddressBook(), exportRequest.getAddressBook());
    }

    @Test
    public void parse_filenameAndCriteria_exportsMatchingContacts() throws ParseException {
        ExportCommand command = parser.parse(" -f meiercontacts -n Meier");

        CommandResult result = command.execute(model);
        ExportRequest exportRequest = result.getExportRequest().orElseThrow();

        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.setPersons(List.of(BENSON, DANIEL));
        assertEquals(expectedAddressBook, exportRequest.getAddressBook());
    }

    @Test
    public void parse_missingFilename_throwsParseException() {
        assertParseFailure("", ExportCommandParser.MESSAGE_MISSING_FILENAME);
        assertParseFailure(" -n Alice", ExportCommandParser.MESSAGE_MISSING_FILENAME);
    }

    @Test
    public void parse_emptyFilename_throwsParseException() {
        assertParseFailure(" -f", "The value for '-f' cannot be empty.");
    }

    @Test
    public void parse_invalidFilename_throwsParseException() {
        assertParseFailure(" -f invalid@filename", Filename.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateFilename_throwsParseException() {
        assertParseFailure(" -f first -f second", "The -f parameter may only be specified once.");
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertParseFailure(" -f samplefile -x value", "Unknown find parameter: '-x'.");
    }

    @Test
    public void parse_unexpectedPreamble_throwsParseException() {
        assertParseFailure("samplefile -f samplefile",
                "Unexpected text before the first parameter: 'samplefile'.");
    }

    private void assertParseFailure(String userInput, String expectedMessage) {
        try {
            parser.parse(userInput);
        } catch (ParseException e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        assertTrue(false, "Expected ParseException for input: " + userInput);
    }
}
