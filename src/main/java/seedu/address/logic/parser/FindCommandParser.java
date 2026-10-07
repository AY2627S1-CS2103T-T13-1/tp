package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Prefix[] FIND_PREFIXES = {
        PREFIX_NAME
    };

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap arguments = extractArguments(args);
            Optional<String> name = arguments.getValue(PREFIX_NAME);

            if (name.isPresent()) {
                ParserUtil.parseName(name.get());
            }

            return new FindCommand(new PersonMatchesCriteriaPredicate(name));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n" + FindCommand.MESSAGE_USAGE, e);
        }
    }

    /**
     * Extracts optional search values and validates the argument structure.
     *
     * @throws ParseException if the argument structure is invalid
     */
    private ArgumentMultimap extractArguments(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, FIND_PREFIXES);
        validateArgumentStructure(args, arguments);
        return arguments;
    }

    /**
     * Validates search flags without interpreting their field values.
     * A standalone token starting with '-' is reserved for a search flag.
     */
    private void validateArgumentStructure(String args, ArgumentMultimap arguments) throws ParseException {
        for (String token : args.trim().split("\\s+")) {
            if (token.startsWith("-") && token.length() > 1
                    && Stream.of(FIND_PREFIXES).noneMatch(prefix -> prefix.getPrefix().equals(token))) {
                throw new ParseException("Unknown find parameter: '" + token + "'.");
            }
        }

        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException("Unexpected text before the first parameter: '"
                    + arguments.getPreamble() + "'.");
        }

        if (Stream.of(FIND_PREFIXES).noneMatch(prefix -> arguments.getValue(prefix).isPresent())) {
            throw new ParseException(FindCommand.MESSAGE_NO_CRITERION);
        }

        arguments.verifyNoDuplicatePrefixesFor(FIND_PREFIXES);

        for (Prefix prefix : FIND_PREFIXES) {
            if (arguments.getValue(prefix).filter(String::isEmpty).isPresent()) {
                throw new ParseException("The value for '" + prefix + "' cannot be empty");
            }
        }
    }

}
