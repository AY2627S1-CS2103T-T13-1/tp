package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new PinCommand object.
 */
public class PinCommandParser implements Parser<PinCommand> {
    public static final String MESSAGE_BLANK_ARGUMENT = "List index was not supplied. "
            + "Please supply a positive integer as a list index.";

    @Override
    public PinCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new PinCommand(index);
        } catch (ParseException pe) {
            if (args.isEmpty()) {
                throw new ParseException(MESSAGE_BLANK_ARGUMENT);
            } else {
                throw new ParseException(
                        String.format(ParserUtil.MESSAGE_INVALID_INDEX, PinCommand.MESSAGE_USAGE), pe);
            }
        }
    }

}
