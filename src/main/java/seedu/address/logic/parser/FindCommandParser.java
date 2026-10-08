package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.model.tag.Tag;

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
            ArgumentMultimap arguments = extractArguments(args);
            Optional<String> name = arguments.getValue(PREFIX_NAME)
                    .map(value -> value.trim().replaceAll("\\s+", " "));
            Optional<String> phone = arguments.getValue(PREFIX_PHONE);
            Optional<String> email = arguments.getValue(PREFIX_EMAIL);
            Optional<String> address = arguments.getValue(PREFIX_ADDRESS);
            Optional<String> tagValue = arguments.getValue(PREFIX_TAG);
            Optional<Tag> tag = Optional.empty();

            if (name.isPresent()) {
                ParserUtil.parseName(name.get());
            }
            if (phone.isPresent()) {
                ParserUtil.parsePhone(phone.get());
            }
            if (email.isPresent()) {
                ParserUtil.parseEmail(email.get());
            }
            if (address.isPresent()) {
                ParserUtil.parseAddress(address.get());
            }
            if (tagValue.isPresent()) {
                tag = Optional.of(ParserUtil.parseTag(tagValue.get()));
            }

            return new FindCommand(new PersonMatchesCriteriaPredicate(name, phone, email, address, tag));
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

        for (Prefix prefix : FIND_PREFIXES) {
            if (arguments.getAllValues(prefix).size() > 1) {
                throw new ParseException("The " + prefix + " parameter may only be specified once.");
            }

            if (arguments.getValue(prefix).filter(String::isEmpty).isPresent()) {
                throw new ParseException("The value for '" + prefix + "' cannot be empty.");
            }
        }
    }

}
