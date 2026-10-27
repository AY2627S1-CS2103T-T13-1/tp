package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.alias.AliasRegistry;

/**
 * Manages storage of address book data, user preferences and command aliases.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonAddressBookStorage addressBookStorage;
    private JsonUserPrefsStorage userPrefsStorage;
    private JsonAliasStorage jsonAliasStorage;

    /**
     * Creates a {@code StorageManager} with the given address book,
     * user prefs storage and alias storage.
     */
    public StorageManager(JsonAddressBookStorage addressBookStorage, JsonUserPrefsStorage userPrefsStorage,
                          JsonAliasStorage jsonAliasStorage) {
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.jsonAliasStorage = jsonAliasStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + addressBookStorage.getAddressBookFilePath());
        return addressBookStorage.readAddressBook();
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        logger.fine("Attempting to write to data file: " + addressBookStorage.getAddressBookFilePath());
        addressBookStorage.saveAddressBook(addressBook);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path path) throws IOException {
        addressBookStorage.saveAddressBook(addressBook, path);
    }

    // ================ Alias methods =====================================

    @Override
    public Path getAliasPath() {
        return jsonAliasStorage.getAliasPath();
    }

    @Override
    public Optional<AliasRegistry> readAliases() throws DataLoadingException {
        return jsonAliasStorage.readAliases();
    }

    @Override
    public void saveAliases(AliasRegistry aliases) throws IOException {
        jsonAliasStorage.saveAliases(aliases);
    }
}
