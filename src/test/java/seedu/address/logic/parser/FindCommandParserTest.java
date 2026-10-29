package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.model.tag.FacultyTag;
import seedu.address.model.tag.ModuleTag;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {
    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noCriteria_throwsParseException() {
        assertParseFailure(parser, "",
                FindCommand.MESSAGE_NO_CRITERION + "\n" + FindCommand.MESSAGE_USAGE);
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
    }

    @Test
    public void parse_unknownCriteria_throwsParseException() {
        assertParseFailure(parser, " -f contacts",
                "Unknown find parameter: '-f'.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -f contacts -n Wolf Alice",
                "Unknown find parameter: '-f'.\n" + FindCommand.MESSAGE_USAGE);
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
    public void parse_invalidEarlierTag_throwsParseException() {
        assertParseFailure(parser, " -t unsupported friends -t others owesMoney",
                Tag.MESSAGE_UNKNOWN_TAG_CATEGORY + "\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others bad@tag -t others owesMoney",
                Tag.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others -archived -t others owesMoney",
                Tag.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidFieldValue_throwsParseException() {
        assertParseFailure(parser, " -n Alice@",
                Name.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_tagWithoutName_throwsParseException() {
        assertParseFailure(parser, " -t others",
                Tag.MESSAGE_MISSING_TAG_FIELD + "\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t -archived",
                Tag.MESSAGE_MISSING_TAG_FIELD + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_hyphenLeadingTagName_throwsParseException() {
        assertParseFailure(parser, " -t others -archived",
                Tag.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_hyphenLeadingWordInTagName_throwsParseException() {
        assertParseFailure(parser, " -t others coder -y",
                Tag.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_hyphenWithinTagName_throwsParseException() {
        assertParseFailure(parser, " -t others full-stack",
                Tag.MESSAGE_CONSTRAINTS + "\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyTagAmongMultipleTags_throwsParseException() {
        String expectedMessage = "The value for '-t' cannot be empty.\n" + FindCommand.MESSAGE_USAGE;
        assertParseFailure(parser, " -t -t others friends", expectedMessage);
        assertParseFailure(parser, " -t others friends -t -t others owesMoney", expectedMessage);
        assertParseFailure(parser, " -t others friends -t", expectedMessage);
    }

    @Test
    public void parse_unexpectedTextBeforeFlag_throwsParseException() {
        assertParseFailure(parser, " Wolf Alice -n Wolf",
                "Unexpected text before the first parameter: 'Wolf Alice'.\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " \t Wolf Alice \t -p 91230000",
                "Unexpected text before the first parameter: 'Wolf Alice'.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyFieldAndDuplicateField_throwsParseException() {
        assertParseFailure(parser, " -n Jacob -n",
                "The value for '-n' cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emailSubstring_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.of("bob"),
                Optional.empty(),
                Set.of());
        assertParseSuccess(parser, " -e bob", new FindCommand(predicate));
    }

    @Test
    public void parse_phoneSubstring_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.of("2"),
                Optional.empty(),
                Optional.empty(),
                Set.of());

        assertParseSuccess(parser, " -p 2", new FindCommand(predicate));
    }

    @Test
    public void parse_validArgsInDifferentOrder_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Set.of(new OthersTag("friend")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " -n Wolf Alice -a Block ABC, Hougang -t others friend -e alice@example.com -p 91230000",
                expectedFindCommand);
    }

    @Test
    public void parse_multipleTags_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Set.of(new OthersTag("friend"),
                        new FacultyTag("Science"),
                        new ModuleTag("BS3402")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " -n Wolf Alice -p 91230000 -e alice@example.com -a Block ABC, Hougang "
                        + "-t others friend -t Module BS3402 -t faculty Science",
                expectedFindCommand);
    }

    @Test
    public void parse_multipleTagsInSameCategory_preservesAllTags() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("friends"), new OthersTag("owesMoney")));
        FindCommand expectedFindCommand = new FindCommand(predicate);

        assertParseSuccess(parser, " -t others friends -t others owesMoney", expectedFindCommand);
        assertParseSuccess(parser, " -t others owesMoney -t others friends", expectedFindCommand);
    }

    @Test
    public void parse_duplicateTags_returnsFindCommandWithOneTag() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("friends")));
        FindCommand expectedFindCommand = new FindCommand(predicate);

        assertParseSuccess(parser, " -t others friends -t others friends", expectedFindCommand);
    }

    @Test
    public void parse_nameWithInternalTab_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of());
        assertParseSuccess(parser, " -n Wolf\tAlice", new FindCommand(predicate));
    }

    @Test
    public void parse_multipleCriteriaWithWhitespace_returnsFindCommand() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Wolf Alice"),
                Optional.of("91230000"),
                Optional.of("alice@example.com"),
                Optional.of("Block ABC, Hougang"),
                Set.of(new OthersTag("friend")));
        FindCommand expectedFindCommand = new FindCommand(predicate);
        assertParseSuccess(parser,
                " \t\n -n \t Wolf   Alice \t\n -p \t 91230000 \t\n"
                        + " -e \t alice@example.com \t\n -a \t Block   ABC,   Hougang \t\n"
                        + " -t \t others \t friend \t\n ",
                expectedFindCommand);
    }

}
