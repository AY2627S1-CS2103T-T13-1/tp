package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.tag.FacultyTag;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing of index and tag deletion arguments, including validation messages and usage text.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_unknownParameterOnly_throwsParseException() {
        assertUnknownParameterFailure(" -y");
    }

    @Test
    public void parse_unknownParameterAfterIndex_throwsParseException() {
        assertUnknownParameterFailure(" 1 -y");
    }

    @Test
    public void parse_unknownParameterBeforeTag_throwsParseException() {
        assertUnknownParameterFailure(" -y -t others coder");
    }

    @Test
    public void parse_hyphenInTagName_throwsParseException() {
        assertParseFailure(parser, " -t others coder -y value",
                Tag.MESSAGE_CONSTRAINTS + "\n" + DeleteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others -archived",
                Tag.MESSAGE_CONSTRAINTS + "\n" + DeleteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others -archived!",
                Tag.MESSAGE_CONSTRAINTS + "\n" + DeleteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -t others full-stack",
                Tag.MESSAGE_CONSTRAINTS + "\n" + DeleteCommand.MESSAGE_USAGE);

    }

    @Test
    public void parse_multipleTags_returnsDeleteCommand() {
        DeleteCommand expected = new DeleteCommand(Set.of(new OthersTag("coder"), new FacultyTag("Science")));
        assertParseSuccess(parser, " -t others coder -t faculty Science", expected);
    }

    @Test
    public void parse_multiwordTagNameWithNumber_returnsDeleteCommand() {
        DeleteCommand expected = new DeleteCommand(Set.of(new OthersTag("coder 3")));
        assertParseSuccess(parser, " -t others coder 3", expected);
    }

    @Test
    public void parse_indexBeforeTags_throwsParseException() {
        assertParseFailure(parser, "3 -t others coder",
                DeleteCommandParser.MESSAGE_TAG_AND_INDEX_PROVIDED
                        + "\n" + DeleteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(
                parser, "a", String.format(ParserUtil.MESSAGE_INVALID_INDEX, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(
                parser, "-1", String.format(ParserUtil.MESSAGE_INVALID_INDEX, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_blankArg_throwsParseException() {
        assertParseFailure(parser, "",
                DeleteCommandParser.MESSAGE_BLANK_ARGUMENT + "\n" + DeleteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_whitespaceOnly_throwsParseException() {
        assertParseFailure(parser, " \t  ",
                DeleteCommandParser.MESSAGE_BLANK_ARGUMENT + "\n" + DeleteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyTagValue_throwsParseException() {
        assertParseFailure(parser, " -t",
                Tag.MESSAGE_MISSING_TAG_FIELD + "\n" + DeleteCommand.MESSAGE_USAGE);
    }

    /**
     * Checks that the unsupported '-y' parameter produces its error message followed by delete usage.
     */
    private void assertUnknownParameterFailure(String args) {
        assertParseFailure(
                parser, args, "Unknown delete parameter: '-y'.\n" + DeleteCommand.MESSAGE_USAGE);
    }
}
