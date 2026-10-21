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
            validateParameters(args);
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
            String message = pe.getMessage();
            if (ParserUtil.MESSAGE_INVALID_INDEX.equals(message)) {
                message = String.format(message, "").stripTrailing();
            }
            throw new ParseException(message + "\n" + DeleteCommand.MESSAGE_USAGE, pe);
        }
    }

    /**
     * Rejects parameters starting with '-' other than the supported tag parameter or negative integers.
     * Negative integers retain their existing index validation or tag-name interpretation.
     * Hyphens within a tag name, such as {@code full-stack}, remain valid.
     *
     * @throws ParseException if an unknown parameter is supplied.
     */
    private void validateParameters(String args) throws ParseException {
        for (String token : args.trim().split("\\s+")) {
            boolean isUnknownFlag = token.startsWith("-")
                    && token.length() > 1
                    && !token.equals(PREFIX_TAG.getPrefix())
                    && !token.matches("-\\d+");
            if (isUnknownFlag) {
                throw new ParseException("Unknown delete parameter: '" + token + "'.");
            }
        }
    }
}
