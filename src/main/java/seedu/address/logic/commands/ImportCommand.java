package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;

import java.nio.file.Path;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class ImportCommand extends Command{
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

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        ImportRequest importRequest = new ImportRequest(filePath);

        return new CommandResult(
                MESSAGE_IMPORT_REQUESTED,
                importRequest);
    }

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
