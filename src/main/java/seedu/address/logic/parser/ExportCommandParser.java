package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments for the export command and creates an {@link ExportCommand}.
 *
 * <p>The parser accepts a filename and optional filtering criteria such as name, phone,
 * email, address, and tags.</p>
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    public static final String MESSAGE_MISSING_FILENAME = "Please provide at least a filename.";

    private static final Prefix[] EXPORT_PREFIXES = {
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

            if (fileName.isEmpty()) {
                throw new ParseException(MESSAGE_MISSING_FILENAME + "\n" + ExportCommand.MESSAGE_USAGE);
            }

            ParserUtil.parseFilename(fileName.get());
            SearchCriteria criteria = SearchCriteriaParser.parse(arguments);

            return new ExportCommand(fileName.get(),
                    criteria.toPredicate());
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
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, EXPORT_PREFIXES);
        ArgumentStructureValidator.validate(arguments, EXPORT_PREFIXES, ExportCommand.COMMAND_WORD);
        return arguments;
    }
}
