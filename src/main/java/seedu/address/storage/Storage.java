package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.alias.AliasRegistry;

/**
 * API of the Storage component
 */
public interface Storage {

    /**
     * Returns the file path of the UserPrefs data file.
     */
    Path getUserPrefsFilePath();

    /**
     * Returns UserPrefs data from storage.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from preference file failed.
     */
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    /**
     * Saves the given {@link seedu.address.model.ReadOnlyUserPrefs} to the storage.
     * @param userPrefs cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    /**
     * Returns the file path of the AddressBook data file.
     */
    Path getAddressBookFilePath();

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the specified file path.
     *
     * @param addressBook the address book to save; cannot be null
     * @param path the file path to save the address book to; cannot be null
     * @throws IOException if there was any problem writing to the file
     */
    void saveAddressBook(ReadOnlyAddressBook addressBook, Path path) throws IOException;

    /**
     * Returns the {@code Path} of the alias data file.
     */
    Path getAliasPath();

    /**
     * Returns alias data as an {@link AliasRegistry}.
     * Returns {@code Optional.empty()} if the storage file is not found.
     * Invalid alias mappings are skipped with warnings, while valid mappings
     * are retained and read into memory.
     *
     * @throws DataLoadingException if the file cannot be read or deserialized.
     */
    Optional<AliasRegistry> readAliases() throws DataLoadingException;

    /**
     * Saves the given {@link AliasRegistry} to storage.
     *
     * @param aliases The aliases to be saved, which cannot be null.
     * @throws IOException if there were issues writing to the data file.
     */
    void saveAliases(AliasRegistry aliases) throws IOException;

}
