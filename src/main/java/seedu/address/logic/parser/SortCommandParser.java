package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;

/**
 * Parses input arguments and creates a new SortCommand object.
 */
public class SortCommandParser implements Parser<SortCommand> {

    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] arguments = parseArguments(args);

        String sortCriteriaString = arguments[0];
        String sortOrderString = arguments[1];

        try {
            SortCriteria sortCriteria = SortCriteria.valueOf(sortCriteriaString.toUpperCase(Locale.ROOT));
            SortOrder sortOrder = SortOrder.valueOf(sortOrderString.toUpperCase(Locale.ROOT));
            return new SortCommand(sortCriteria, sortOrder);
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }

    private String[] parseArguments(String args) throws ParseException {
        String[] arguments = args.trim().split("\\s+", 3);

        if (arguments.length != 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return arguments;
    }
}
