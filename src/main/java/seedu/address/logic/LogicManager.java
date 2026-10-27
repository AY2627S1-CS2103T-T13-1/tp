package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExportRequest;
import seedu.address.logic.commands.ImportRequest;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ImportSummary;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    public static final String FILE_IMPORT_ERROR_FORMAT =
            "Could not import contacts from %s: %s";

    public static final String FILE_NOT_FOUND = "file was not found.";

    public static final String FILE_INVALID_FORMAT =
            "The file is not a valid address book JSON file.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private boolean hasUnsavedContactChanges;
    private boolean hasUnsavedAliasChanges;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser(model.getAliasRegistry());
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());
        Map<String, String> originalAliases = model.getAliases();

        CommandResult commandResult = command.execute(model);

        boolean isImport = commandResult.getImportRequest().isPresent();

        try {
            if (isImport) {
                commandResult = executeImport(commandResult.getImportRequest().get());
            }

            model.commit();

            boolean contactsChanged = !originalAddressBook.equals(model.getAddressBook());
            boolean aliasChanged = !originalAliases.equals(model.getAliases());

            // account for pending saves from failed attempt
            hasUnsavedContactChanges = hasUnsavedContactChanges || contactsChanged;
            hasUnsavedAliasChanges = hasUnsavedAliasChanges || aliasChanged;

            if (commandResult.getExportRequest().isPresent()) {
                ExportRequest exportRequest = commandResult.getExportRequest().get();

                storage.saveAddressBook(
                        exportRequest.getAddressBook(),
                        exportRequest.getFilePath());

            } else if (hasUnsavedContactChanges || isImport) {
                storage.saveAddressBook(model.getAddressBook());
                hasUnsavedContactChanges = false;
            }

            savePendingAliasChanges();

        } catch (DataLoadingException e) {
            throw new CommandException(
                    String.format(
                            FILE_IMPORT_ERROR_FORMAT,
                            commandResult.getImportRequest().orElseThrow().getFilePath(),
                            getImportErrorMessage(e)),
                    e);
        } catch (AccessDeniedException e) {
            throw new CommandException(
                    String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException e) {
            throw new CommandException(
                    String.format(FILE_OPS_ERROR_FORMAT, e.getMessage()), e);
        }

        return commandResult;
    }

    private String getImportErrorMessage(DataLoadingException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof IllegalValueException || cause instanceof IllegalArgumentException) {
                return cause.getMessage();
            }
            cause = cause.getCause();
        }
        return FILE_INVALID_FORMAT;
    }

    private CommandResult executeImport(ImportRequest importRequest)
            throws DataLoadingException, CommandException {
        ReadOnlyAddressBook importedAddressBook = storage
                .readAddressBook(importRequest.getFilePath())
                .orElseThrow(() -> new CommandException(
                        String.format(
                                FILE_IMPORT_ERROR_FORMAT,
                                importRequest.getFilePath(), FILE_NOT_FOUND)));

        ImportSummary summary = model.mergeAddressBook(importedAddressBook);
        return new CommandResult(summary.getMessage());
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public void savePendingAliasChanges() throws CommandException {
        if (!hasUnsavedAliasChanges) {
            return;
        }

        try {
            storage.saveAliases(model.getAliasRegistry());
            hasUnsavedAliasChanges = false;
        } catch (AccessDeniedException exception) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT,
                    exception.getMessage()), exception);
        } catch (IOException exception) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT,
                    exception.getMessage()), exception);
        }
    }
}
