package seedu.address.logic.commands;


import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILENAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Exports contacts that match the specified predicate to a file.
 *
 * If no filtering criteria is provided, all contacts are exported by default.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String COMMAND_DESCRIPTION = "Exports contact(s) to a .json file";

    public static final String EXPORT_APPEND_FILETYPE = ".json";

    public static final String SUCCESS_MESSAGE = "Contact(s) exported to %s";

    public static final String EXPORT_FAILED_NONE_MATCH = "No contacts with the specified field(s) found!";
    private static final Path EXPORT_DIRECTORY = Paths.get("data", "exports");
    public static final String COMMAND_SYNTAX = COMMAND_WORD + " "
            + PREFIX_FILENAME + " FILENAME "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PHONE + " PHONE_NUMBER] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " TAG_CATEGORY TAG_NAME]...";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " "
            + PREFIX_FILENAME + " exports "
            + PREFIX_NAME + " Alice "
            + PREFIX_TAG + " others coder";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: "
            + PREFIX_FILENAME + " FILENAME "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PHONE + " PHONE_NUMBER] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " TAG_CATEGORY TAG_NAME]...\n"
            + COMMAND_SAMPLE_USAGE;
    private final Predicate<Person> predicate;
    private final String fileName;

    /**
     * Constructs an {@code ExportCommand} with the specified filename and contact filter.
     *
     * @param fileName the name of the file to export to
     * @param predicate the predicate used to select contacts for export
     */
    public ExportCommand(String fileName, Predicate<Person> predicate) {
        this.predicate = predicate;
        this.fileName = fileName + EXPORT_APPEND_FILETYPE;
    }

    /**
     * Exports all contacts that satisfy the command's predicate.
     *
     * @param model the model containing the contacts to export
     * @return a {@code CommandResult} containing the export request
     */
    public CommandResult execute(Model model) {
        requireNonNull(model);

        AddressBook exportBook = new AddressBook();

        List<Person> matches = model.getAddressBook()
                .getPersonList()
                .stream().filter(this.predicate).toList();

        if (matches.isEmpty()) {
            return new CommandResult(EXPORT_FAILED_NONE_MATCH);
        }
        exportBook = new AddressBook();
        exportBook.setPersons(matches);

        ExportRequest exportRequest = new ExportRequest(exportBook, EXPORT_DIRECTORY.resolve(fileName));
        return new CommandResult(String.format(SUCCESS_MESSAGE, this.fileName), exportRequest);
    }
}
