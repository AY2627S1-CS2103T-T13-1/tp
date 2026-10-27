package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;

import java.nio.file.Path;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Imports contacts from a JSON file and merges them into the current address book.
 * Contacts that have the same phone number or email address as an existing contact
 * are skipped during the merge.
 *
 * This command creates an {@link ImportRequest}. The file is read and processed
 * by the logic and storage components after the command is executed.
 */
public class ImportCommand extends Command {
    public static final String COMMAND_WORD = "import";
    public static final String COMMAND_DESCRIPTION = "Imports contacts from a .json file and skips duplicates.";
    public static final String MESSAGE_IMPORT_REQUESTED =
            "Importing contacts...";

    public static final String COMMAND_SYNTAX =
            COMMAND_WORD + " " + PREFIX_FILENAME + " FILE_PATH";

    public static final String COMMAND_SAMPLE_USAGE =
            "Example: " + COMMAND_WORD
                    + " " + PREFIX_FILENAME
                    + " data/exports/contacts.json";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": "
                    + COMMAND_DESCRIPTION
                    + "\nParameters: "
                    + PREFIX_FILENAME
                    + " FILE_PATH\n"
                    + COMMAND_SAMPLE_USAGE;

    private final Path filePath;

    /**
     * Creates an ImportCommand for the specified file.
     *
     * @param filePath path of the JSON file to import
     */
    public ImportCommand(Path filePath) {
        requireNonNull(filePath);
        this.filePath = filePath;
    }

    /**
     * Creates an import request for the configured file path.
     *
     * @param model the model on which the command is executed
     * @return a command result containing the import request
     * @throws CommandException if an error occurs during command execution
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        ImportRequest importRequest = new ImportRequest(filePath);

        return new CommandResult(
                MESSAGE_IMPORT_REQUESTED,
                importRequest);
    }

    /**
     * Returns the path of the file to import.
     *
     * @return the import file path
     */
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ImportCommand otherImportCommand)) {
            return false;
        }

        return filePath.equals(otherImportCommand.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}
