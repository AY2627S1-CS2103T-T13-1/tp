package seedu.address;

import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.alias.AliasRegistry;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.AliasLoadResult;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonAliasStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.5";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "addressbook.json");
    private static final Path ALIAS_FILE_PATH = Paths.get("data", "aliases.json");

    private static final int MAX_ALIAS_LOADING_ERRORS = 5;

    private static final String MESSAGE_ALIAS_PARTIAL_LOAD_WARNING =
            "Alias file: %s\nLoaded %d alias(es). Skipped %d invalid entries.\n\n%s";

    private static final String MESSAGE_ADDITIONAL_ALIAS_ISSUES =
            "\n\n…and %d more issue(s). See the log for details.";

    private static final String MESSAGE_ALIAS_LOAD_FAILED =
            "Alias file at %s could not be loaded. Starting with empty aliases.";

    private static final String MESSAGE_ALIAS_READ_ACCESS_DENIED =
            "The file could not be read because access was denied.";

    private static final String MESSAGE_ALIAS_LOAD_REASON_UNKNOWN =
            "The file could not be read or has an invalid format.";

    private static final String MESSAGE_ALIAS_LOAD_FAILURE_DETAILS =
            "%s\n\nReason: %s"
                    + "\n\nCorrect the file or resolve the read error, "
                    + "then restart the application.";

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    private String aliasLoadingWarning = "";

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing coNnectUS ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        JsonAliasStorage aliasStorage = new JsonAliasStorage(ALIAS_FILE_PATH, BUILT_IN_COMMAND_WORDS);
        storage = new StorageManager(addressBookStorage, userPrefsStorage, aliasStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Returns a {@code ModelManager} initialized with stored contacts, aliases and {@code userPrefs}.
     * Uses sample contacts if the address book file is not found, or an empty address book
     * if loading fails.
     * Uses empty aliases if the alias file is missing or cannot be loaded.
     * Invalid alias mappings are skipped with warnings, while valid mappings are retained.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            initialData = new AddressBook();
        }

        AliasRegistry initialAliases;
        try {
            // load the aliases
            AliasLoadResult result = storage.readAliases().orElseGet(() ->
                    new AliasLoadResult(new AliasRegistry(BUILT_IN_COMMAND_WORDS), List.of()));
            initialAliases = result.aliases();

            aliasLoadingWarning = formatAliasLoadingWarning(result, storage.getAliasPath());
        } catch (DataLoadingException exception) {
            // log the failure
            String warning = String.format(MESSAGE_ALIAS_LOAD_FAILED, storage.getAliasPath());
            logger.warning(warning);

            aliasLoadingWarning = String.format(MESSAGE_ALIAS_LOAD_FAILURE_DETAILS, warning,
                    getAliasLoadingReason(exception));

            // initialize new empty alias registry on failure
            initialAliases = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        }
        return new ModelManager(initialData, userPrefs, BUILT_IN_COMMAND_WORDS, initialAliases);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting coNnectUS " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping coNnectUS ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }

    /**
     * Formats a loading summary with up to five skipped-entry warnings and the remaining issue count.
     */
    private String formatAliasLoadingWarning(AliasLoadResult result, Path path) {
        List<String> warnings = result.warnings();
        if (warnings.isEmpty()) {
            return "";
        }

        // prepare only warning items to display
        int displayedCount = Math.min(MAX_ALIAS_LOADING_ERRORS, warnings.size());
        String details = String.join("\n\n", warnings.subList(0, displayedCount));

        int remaining = warnings.size() - displayedCount;
        if (remaining > 0) {
            details += String.format(MESSAGE_ADDITIONAL_ALIAS_ISSUES, remaining);
        }

        return String.format(MESSAGE_ALIAS_PARTIAL_LOAD_WARNING, path, result.loadedAliasCount(),
                warnings.size(), details);
    }

    /**
     * Extracts the underlying loading failure reason.
     */
    private String getAliasLoadingReason(DataLoadingException exception) {
        Throwable cause = exception;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        if (cause instanceof AccessDeniedException) {
            return MESSAGE_ALIAS_READ_ACCESS_DENIED;
        }

        if (cause instanceof JsonProcessingException jsonException) {
            return jsonException.getOriginalMessage();
        }

        String message = cause.getMessage();
        if (message == null || message.isBlank()) {
            return MESSAGE_ALIAS_LOAD_REASON_UNKNOWN;
        }
        return message;
    }
}
