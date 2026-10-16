package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Theme;
import seedu.address.logic.commands.ThemeCommand;

public class ThemeCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE);

    private ThemeCommandParser parser = new ThemeCommandParser();

    @Test
    public void parse_validArgs_returnsThemeCommand() {
        assertParseSuccess(parser, "dark", new ThemeCommand(Theme.DARK));
        assertParseSuccess(parser, "light", new ThemeCommand(Theme.LIGHT));
    }

    @Test
    public void parse_differentCaseAndSpaces_returnsThemeCommand() {
        assertParseSuccess(parser, "  LIGHT  ", new ThemeCommand(Theme.LIGHT));
        assertParseSuccess(parser, "Dark", new ThemeCommand(Theme.DARK));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "blue", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "dark light", MESSAGE_INVALID_FORMAT);
    }
}
