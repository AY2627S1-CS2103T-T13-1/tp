package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnpinCommand;

public class UnpinCommandParserTest {

    private UnpinCommandParser parser = new UnpinCommandParser();

    @Test
    public void parse_validArgs_returnsUnpinCommand() {
        assertParseSuccess(parser, "1", new UnpinCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(ParserUtil.MESSAGE_INVALID_INDEX, UnpinCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_blankArg_throwsParseException() {
        assertParseFailure(parser, "", String.format(UnpinCommandParser.MESSAGE_BLANK_ARGUMENT));
    }
}
