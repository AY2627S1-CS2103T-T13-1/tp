package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_BLANK_ARGUMENT = "List index was not supplied. "
        + "Please supply a positive integer as a list index.";

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteCommand(index);
        } catch (ParseException pe) {
            if (args.isEmpty()) {
                throw new ParseException(MESSAGE_BLANK_ARGUMENT);
            } else {
                throw new ParseException(
                        String.format(ParserUtil.MESSAGE_INVALID_INDEX, DeleteCommand.MESSAGE_USAGE), pe);
            }
        }
    }

}
