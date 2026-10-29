package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.ImportCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.ImportCommandParser.MESSAGE_INVALID_FILE_PATH;
import static seedu.address.logic.parser.ImportCommandParser.MESSAGE_MISSING_FILE_PATH;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ImportCommandParserTest {

    private final ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_validFilePath_returnsImportCommand() throws ParseException {
        ImportCommand command = parser.parse(" -f data/exports/contacts.json");

        assertEquals(new ImportCommand(Path.of("data", "exports", "contacts.json")), command);
    }

    @Test
    public void parse_filePathWithSpaces_returnsImportCommand() throws ParseException {
        ImportCommand command = parser.parse(" -f data/exports/my contacts.json");

        assertEquals(new ImportCommand(Path.of("data", "exports", "my contacts.json")), command);
    }

    @Test
    public void parse_missingFilePath_throwsParseException() {
        assertParseFailure("", MESSAGE_MISSING_FILE_PATH + "\n" + MESSAGE_USAGE);
        assertParseFailure(" ", MESSAGE_MISSING_FILE_PATH + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyFilePath_throwsParseException() {
        assertParseFailure(" -f", "The value for '-f' cannot be empty.");
        assertParseFailure("\u2003-f\u00a0", "The value for '-f' cannot be empty.");
    }

    @Test
    public void parse_invalidFilePath_throwsParseException() {
        String invalidFilePath = "invalid\u0000path";
        assertParseFailure(" -f " + invalidFilePath,
                String.format(MESSAGE_INVALID_FILE_PATH, invalidFilePath));
    }

    @Test
    public void parse_duplicateFilePath_throwsParseException() {
        assertParseFailure(
                " -f first.json -f second.json",
                "The -f parameter may only be specified once.");
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertParseFailure(
                " -f contacts.json -x value",
                "Unknown import parameter: '-x'.");
    }

    @Test
    public void parse_unexpectedPreamble_throwsParseException() {
        assertParseFailure(
                "contacts.json -f contacts.json",
                "Unexpected text before the first parameter: 'contacts.json'.");
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
