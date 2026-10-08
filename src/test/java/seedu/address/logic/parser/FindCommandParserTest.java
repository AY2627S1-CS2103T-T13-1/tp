package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.model.tag.OthersTag;

public class FindCommandParserTest {
    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noCriteria_throwsParseException() {
        assertParseFailure(parser, "     ",
                FindCommand.MESSAGE_NO_CRITERION + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_duplicateCriteria_throwsParseException() {
        assertParseFailure(parser, " -n Wolf Alice -n Wolf",
                "The -n parameter may only be specified once.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -p 91230000 -p 91230001",
                "The -p parameter may only be specified once.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -e alice@example.com -e bob@example.com",
                "The -e parameter may only be specified once.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -a Block ABC -a Block DEF",
                "The -a parameter may only be specified once.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others friend -t others colleague",
                "The -t parameter may only be specified once.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_unknownCriteria_throwsParseException() {
        assertParseFailure(parser, " -x Wolf Alice",
                "Unknown find parameter: '-x'.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -n Wolf Alice -x unknown",
                "Unknown find parameter: '-x'.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyCriteria_throwsParseException() {
        assertParseFailure(parser, " -n",
                "The value for '-n' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -p",
                "The value for '-p' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -e",
                "The value for '-e' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -a",
                "The value for '-a' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t",
                "The value for '-t' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -n -p 91230000",
                "The value for '-n' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_unexpectedTextBeforeFlag_throwsParseException() {
        assertParseFailure(parser, " Wolf Alice -n Wolf",
                "Unexpected text before the first parameter: 'Wolf Alice'.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " \t Wolf Alice \t -p 91230000",
                "Unexpected text before the first parameter: 'Wolf Alice'.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Optional.of(new OthersTag("friend")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " -n Wolf Alice -p 91230000 -e alice@example.com -a Block ABC, Hougang -t others friend",
                expectedFindCommand);
    }

    @Test
    public void parse_validArgsInDifferentOrder_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Optional.of(new OthersTag("friend")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " -n Wolf Alice -a Block ABC, Hougang -t others friend -e alice@example.com -p 91230000",
                expectedFindCommand);
    }

    @Test
    public void parse_multipleCriteriaWithWhitespace_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Optional.of(new OthersTag("friend")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " \t\n -n \t Wolf   Alice \t\n -p \t 91230000 \t\n"
                        + " -e \t alice@example.com \t\n -a \t Block   ABC,   Hougang \t\n"
                        + " -t \t others \t friend \t\n ",
                expectedFindCommand);
    }

}
