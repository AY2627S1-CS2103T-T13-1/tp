package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.alias.AliasRegistry.MESSAGE_REGEX_MISMATCH;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AliasCommand;

public class AliasCommandParserTest {

    private AliasCommandParser parser = new AliasCommandParser();

    @Test
    public void parse_validArgs_returnsAliasCommand() {
        assertParseSuccess(parser, "find search", new AliasCommand("find", "search"));
        assertParseSuccess(parser, "find F", new AliasCommand("find", "F"));
    }

    @Test
    public void parse_invalidAlias_throwsParseException() {
        assertParseFailure(parser, "find 123", MESSAGE_REGEX_MISMATCH);
    }

    @Test
    public void parse_invalidArgumentCount_throwsParseException() {
        assertParseFailure(parser, "find",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));

        assertParseFailure(parser, "find f blah",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));
    }
}
