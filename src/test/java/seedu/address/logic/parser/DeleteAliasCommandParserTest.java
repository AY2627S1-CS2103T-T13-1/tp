package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.alias.AliasRegistry.MESSAGE_REGEX_MISMATCH;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteAliasCommand;

public class DeleteAliasCommandParserTest {
    private static final String INVALID_COMMAND_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAliasCommand.MESSAGE_USAGE);

    private final DeleteAliasCommandParser parser = new DeleteAliasCommandParser();

    @Test
    public void parse_validAlias_returnsDeleteAliasCommand() {
        assertParseSuccess(parser, "f", new DeleteAliasCommand("f"));
        assertParseSuccess(parser, " \tSearch\n ", new DeleteAliasCommand("Search"));
        assertParseSuccess(parser, "\u00a0f\u00a0", new DeleteAliasCommand("f"));
        assertParseSuccess(parser, "\u2003Search\u2028", new DeleteAliasCommand("Search"));
    }

    @Test
    public void parse_invalidAlias_throwsParseException() {
        assertParseFailure(parser, "f123", MESSAGE_REGEX_MISMATCH);
        assertParseFailure(parser, "搜索", MESSAGE_REGEX_MISMATCH);
    }

    @Test
    public void parse_missingAlias_throwsParseException() {
        assertParseFailure(parser, "", INVALID_COMMAND_FORMAT_MESSAGE);
        assertParseFailure(parser, " \t\n ", INVALID_COMMAND_FORMAT_MESSAGE);
        assertParseFailure(parser, "\u00a0\u2003\u2028", INVALID_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, "f extra", INVALID_COMMAND_FORMAT_MESSAGE);
        assertParseFailure(parser, "f\u00a0extra", INVALID_COMMAND_FORMAT_MESSAGE);
    }
}
