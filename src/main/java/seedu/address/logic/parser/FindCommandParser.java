package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Prefix[] FIND_PREFIXES = {
        PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG
    };

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, FIND_PREFIXES);
            ArgumentStructureValidator.validate(
                    args,
                    arguments,
                    FIND_PREFIXES,
                    FindCommand.COMMAND_WORD);

            if (Stream.of(FIND_PREFIXES)
                    .noneMatch(prefix -> arguments.getValue(prefix).isPresent())) {
                throw new ParseException(FindCommand.MESSAGE_NO_CRITERION);
            }

            SearchCriteria criteria = SearchCriteriaParser.parse(arguments);

            return new FindCommand(criteria.toPredicate());
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n" + FindCommand.MESSAGE_USAGE, e);
        }
    }

}
