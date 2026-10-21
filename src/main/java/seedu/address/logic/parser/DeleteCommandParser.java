package seedu.address.logic.parser;


import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_BLANK_ARGUMENT = "List index or tags were not supplied. "
            + "Please supply either a positive integer as a list index or at least one tag.";

    public static final String MESSAGE_TAG_AND_INDEX_PROVIDED = "List index and tags were supplied. "
            + "Please supply either a positive integer as a list index or at least one tag.";

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
            List<String> tags = arguments.getAllValues(PREFIX_TAG);
            if (!tags.isEmpty() && !arguments.getPreamble().isBlank()) {
                throw new ParseException(MESSAGE_TAG_AND_INDEX_PROVIDED);
            }
            Set<Tag> tagList = ParserUtil.parseTags(tags);
            if (args.isBlank()) {
                throw new ParseException(MESSAGE_BLANK_ARGUMENT);
            }
            if (tagList.isEmpty()) {
                Index index = ParserUtil.parseIndex(args);
                return new DeleteCommand(index);
            }
            return new DeleteCommand(tagList);
        } catch (ParseException pe) {
            String message = String.format(pe.getMessage(), "").stripTrailing();
            throw new ParseException(message + "\n" + DeleteCommand.MESSAGE_USAGE, pe);
        }
    }
}
