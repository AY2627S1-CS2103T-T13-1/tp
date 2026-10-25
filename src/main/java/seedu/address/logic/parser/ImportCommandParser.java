package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

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
            "The provided file path is invalid.";
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
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(
                args,
                IMPORT_PREFIXES);

        ArgumentStructureValidator.validate(
                args,
                arguments,
                IMPORT_PREFIXES,
                ImportCommand.COMMAND_WORD);

        Optional<String> filePath = arguments.getValue(PREFIX_FILENAME);

        if (filePath.isEmpty()) {
            throw new ParseException(
                    MESSAGE_MISSING_FILE_PATH
                            + "\n"
                            + ImportCommand.MESSAGE_USAGE);
        }

        String trimmedFilePath = filePath.get().trim();

        if (trimmedFilePath.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_FILE_PATH);
        }

        try {
            return new ImportCommand(Path.of(trimmedFilePath));
        } catch (InvalidPathException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_FILE_PATH, e.getReason()));
        }
    }
}
