package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments for the export command and creates an {@link ExportCommand}.
 *
 * <p>The parser accepts a filename and optional filtering criteria such as name, phone,
 * email, address, and tags.</p>
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    public static final String MESSAGE_MISSING_FILENAME = "Please provide at least a filename.";

    private static final Prefix[] FIND_PREFIXES = {
        PREFIX_FILENAME, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG
    };

    /**
     * Parses the given input arguments and creates an {@link ExportCommand}.
     *
     * @param args the command arguments to parse
     * @return an export command containing the filename and filtering criteria
     * @throws ParseException if the arguments are invalid or the filename is missing
     */
    public ExportCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap arguments = extractArguments(args);
            Optional<String> fileName = arguments.getValue(PREFIX_FILENAME);
            Optional<String> name = arguments.getValue(PREFIX_NAME)
                    .map(value -> value.trim().replaceAll("\\s+", " "));
            Optional<String> phone = arguments.getValue(PREFIX_PHONE);
            Optional<String> email = arguments.getValue(PREFIX_EMAIL);
            Optional<String> address = arguments.getValue(PREFIX_ADDRESS);
            Set<Tag> tagList = ParserUtil.parseTags(arguments.getAllValues(PREFIX_TAG));

            if (fileName.isEmpty()) {
                throw new ParseException(MESSAGE_MISSING_FILENAME + "\n" + ExportCommand.MESSAGE_USAGE);
            } else {
                ParserUtil.parseFilename(fileName.get());
            }

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

            return new ExportCommand(fileName.get(),
                    new PersonMatchesCriteriaPredicate(name, phone, email, address, tagList));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    /**
     * Tokenizes and validates the arguments supplied to the export command.
     *
     * @param args the command arguments to extract
     * @return a map containing the parsed prefixes and values
     * @throws ParseException if the argument structure is invalid
     */
    private ArgumentMultimap extractArguments(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, FIND_PREFIXES);
        validateArgumentStructure(args, arguments);
        return arguments;
    }

    /**
     * Validates the structure of the export command arguments.
     *
     * <p>This checks for unknown prefixes, unexpected text, missing criteria,
     * empty values, and duplicate non-tag parameters.</p>
     *
     * @param args the original command arguments
     * @param arguments the tokenized command arguments
     * @throws ParseException if the argument structure is invalid
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
            throw new ParseException(MESSAGE_MISSING_FILENAME + "\n" + ExportCommand.MESSAGE_USAGE);
        }

        for (Prefix prefix : FIND_PREFIXES) {
            if (arguments.getAllValues(prefix).stream().anyMatch(String::isEmpty)) {
                throw new ParseException("The value for '" + prefix + "' cannot be empty.");
            }

            if (arguments.getAllValues(prefix).size() > 1 && !prefix.equals(PREFIX_TAG)) {
                throw new ParseException("The " + prefix + " parameter may only be specified once.");
            }
        }
    }
}
