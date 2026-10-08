package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

public class FindCommandParserTest {
    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                FindCommand.MESSAGE_NO_CRITERION + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser, " -n Wolf Alice", expectedFindCommand);
    }

    @Test
    public void parse_validArgsWithWhitespace_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser, " \n -n \t Wolf   Alice  \t", expectedFindCommand);
    }

}
