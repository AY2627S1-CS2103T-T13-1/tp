package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.alias.AliasRegistry;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparatorFactory;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final AliasRegistry aliasRegistry;
    private Comparator<Person> activeComparator;

    /**
     * Initializes a ModelManager with the given addressBook, userPrefs and empty aliases.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs,
                        Set<String> builtInCommandWords) {
        this(addressBook, userPrefs, builtInCommandWords, new AliasRegistry(builtInCommandWords));
    }

    /**
     * Initializes a ModelManager with the given address book, user prefs and aliases.
     * Copies the supplied aliases so subsequent changes to them do not affect the model.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs,
                        Set<String> builtInCommandWords, AliasRegistry aliasRegistry) {
        requireAllNonNull(addressBook, userPrefs, builtInCommandWords, aliasRegistry);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        // Sorts the initial data first, so that undoing back to it gives the same order that is displayed.
        AddressBook sortedAddressBook = new AddressBook(addressBook);
        this.addressBook = new VersionedAddressBook(sortedAddressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        this.aliasRegistry = new AliasRegistry(builtInCommandWords);
        aliasRegistry.getAliases().forEach((alias, commandWord) ->
                this.aliasRegistry.register(commandWord, alias));
        this.activeComparator = PersonComparatorFactory.createDefaultComparator();
        sortedAddressBook.sortPersons(this.activeComparator);
    }

    public ModelManager(Set<String> builtInCommandWords) {
        this(new AddressBook(), new UserPrefs(), builtInCommandWords);
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        sortAddressBook(activeComparator);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        sortAddressBook(activeComparator);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        sortAddressBook(activeComparator);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
        sortAddressBook(activeComparator);
    }

    //=========== Undo and redo ==============================================================================

    @Override
    public void commit() {
        if (addressBook.commit()) {
            logger.fine("Saved a new address book state");
        }
    }

    @Override
    public boolean canUndo() {
        return addressBook.canUndo();
    }

    @Override
    public void undo() {
        addressBook.undo();
        sortAddressBook(this.activeComparator);
        logger.info("Restored the previous address book state");
    }

    @Override
    public boolean canRedo() {
        return addressBook.canRedo();
    }

    @Override
    public void redo() {
        addressBook.redo();
        sortAddressBook(activeComparator);
        logger.info("Restored the most recently undone address book state");
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && aliasRegistry.equals(otherModelManager.aliasRegistry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressBook, userPrefs, filteredPersons, aliasRegistry);
    }

    @Override
    public void sortAddressBook(Comparator<Person> comp) {
        requireNonNull(comp);
        addressBook.sortPersons(comp);
        activeComparator = comp;
    }

    @Override
    public AliasRegistry getAliasRegistry() {
        return aliasRegistry;
    }

    @Override
    public void registerAlias(String commandWord, String alias) {
        aliasRegistry.register(commandWord, alias);
    }

    @Override
    public Map<String, String> getAliases() {
        return aliasRegistry.getAliases();
    }

    @Override
    public void deleteAlias(String alias) {
        aliasRegistry.deleteAlias(alias);
    }

    @Override
    public ImportSummary mergeAddressBook(ReadOnlyAddressBook importedAddressBook) {
        requireNonNull(importedAddressBook);

        AddressBook mergedAddressBook = new AddressBook(addressBook);

        int importedCount = 0;
        int skippedCount = 0;

        for (Person importedPerson : importedAddressBook.getPersonList()) {
            if (mergedAddressBook.hasPerson(importedPerson)) {
                skippedCount++;
            } else {
                mergedAddressBook.addPerson(importedPerson);
                importedCount++;
            }
        }

        setAddressBook(mergedAddressBook);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new ImportSummary(importedCount, skippedCount);
    }
}
