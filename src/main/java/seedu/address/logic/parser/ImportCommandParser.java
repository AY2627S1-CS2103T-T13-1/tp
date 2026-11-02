package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments supplied to the import command.
 *
 * The parser accepts a file path provided with the {@code -f} prefix and
 * creates an {@link ImportCommand} for the path.
 */
public class ImportCommandParser implements Parser<ImportCommand> {
    public static final String MESSAGE_MISSING_FILE_PATH =
            "Please provide a file path.";

    public static final String MESSAGE_INVALID_FILE_PATH =
            "The provided file path is invalid: %s.";
    public static final String MESSAGE_FILENAME_CONTAINS_QUOTES =
            "File path must not contain double quotes.";
    public static final String MESSAGE_UNMATCHED_QUOTES =
            "The file path contains unmatched double quotes.";
    public static final String MESSAGE_TEXT_AFTER_CLOSING_QUOTE =
            "The file path contains text after the closing quote.";

    private static final Prefix[] IMPORT_PREFIXES = {
        PREFIX_FILENAME
    };
    /**
     * Parses the supplied arguments and creates an import command.
     *
     * @param args the command arguments to parse
     * @return an import command containing the specified file path
     * @throws ParseException if the file path is missing, empty, or invalid,
     *         or if the argument structure is invalid
     */
    @Override
    public ImportCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, true, IMPORT_PREFIXES);

        ArgumentStructureValidator.validate(arguments, IMPORT_PREFIXES, ImportCommand.COMMAND_WORD, true);

        Optional<String> filePath = arguments.getValue(PREFIX_FILENAME);

        if (filePath.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_FILE_PATH + "\n" + ImportCommand.MESSAGE_USAGE);
        }

        String trimmedFilePath = parseQuotedFilePath(filePath.get());

        try {
            return new ImportCommand(Path.of(trimmedFilePath));
        } catch (InvalidPathException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_FILE_PATH, trimmedFilePath));
        }
    }

    /**
     * Parses and validates a raw file path that may be enclosed in double quotes.
     *
     * @throws ParseException if the quotes are unmatched, the file path contains
     *         additional double quotes, or the file path is empty
     */
    private String parseQuotedFilePath(String rawFilePath) throws ParseException {
        String filePath = StringUtil.stripWhitespace(rawFilePath);

        if (filePath.startsWith("\"")) {
            int closingQuoteIndex = filePath.indexOf('"', 1);
            String remainder = filePath.substring(closingQuoteIndex + 1);

            if (!remainder.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_TEXT_AFTER_CLOSING_QUOTE, remainder));
            }

            filePath = filePath.substring(1, closingQuoteIndex);
        }

        if (filePath.contains("\"")) {
            throw new ParseException(MESSAGE_FILENAME_CONTAINS_QUOTES);
        }

        if (StringUtil.stripWhitespace(filePath).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_FILE_PATH + "\n" + ImportCommand.MESSAGE_USAGE);
        }

        return filePath;
    }
}
