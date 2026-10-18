package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PinCommand;

public class PinCommandParserTest {

    private PinCommandParser parser = new PinCommandParser();

    @Test
    public void parse_validArgs_returnsPinCommand() {
        assertParseSuccess(parser, "1", new PinCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(ParserUtil.MESSAGE_INVALID_INDEX, PinCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_blankArg_throwsParseException() {
        assertParseFailure(parser, "", String.format(PinCommandParser.MESSAGE_BLANK_ARGUMENT));
    }
}
