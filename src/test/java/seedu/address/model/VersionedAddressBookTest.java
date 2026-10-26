package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparatorFactory;
import seedu.address.testutil.AddressBookBuilder;

public class VersionedAddressBookTest {

    private static final Comparator<Person> DEFAULT_COMPARATOR = PersonComparatorFactory.createDefaultComparator();

    private final AddressBook addressBookWithAlice = new AddressBookBuilder().withPerson(ALICE).build();
    private final AddressBook addressBookWithAliceAndBenson =
            new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();

    @Test
    public void constructor_validInitialState_copiesInitialState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        assertEquals(addressBookWithAlice, versionedAddressBook);
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void commit_dataChanged_savesState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);

        assertTrue(versionedAddressBook.commit(DEFAULT_COMPARATOR));
        assertTrue(versionedAddressBook.canUndo());
    }

    @Test
    public void commit_dataUnchanged_doesNotSaveState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);

        assertFalse(versionedAddressBook.commit(DEFAULT_COMPARATOR));
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void undo_previousStateExists_restoresPreviousState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);

        versionedAddressBook.undo();

        assertEquals(addressBookWithAlice, versionedAddressBook);
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void undo_multipleStates_restoresStatesInReverseOrder() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(new AddressBook());
        versionedAddressBook.addPerson(ALICE);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);

        versionedAddressBook.undo();
        assertEquals(addressBookWithAlice, versionedAddressBook);

        versionedAddressBook.undo();
        assertEquals(new AddressBook(), versionedAddressBook);
    }

    @Test
    public void undo_noPreviousState_throwsIllegalStateException() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        assertThrows(IllegalStateException.class, versionedAddressBook::undo);
    }

    @Test
    public void redo_undoneStateExists_restoresUndoneState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit();
        versionedAddressBook.undo();

        versionedAddressBook.redo();

        assertEquals(addressBookWithAliceAndBenson, versionedAddressBook);
        assertTrue(versionedAddressBook.canUndo());
        assertFalse(versionedAddressBook.canRedo());
    }

    @Test
    public void redo_multipleUndoneStates_restoresStatesInOrder() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(new AddressBook());
        versionedAddressBook.addPerson(ALICE);
        versionedAddressBook.commit();
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit();
        versionedAddressBook.undo();
        versionedAddressBook.undo();

        versionedAddressBook.redo();
        assertEquals(addressBookWithAlice, versionedAddressBook);

        versionedAddressBook.redo();
        assertEquals(addressBookWithAliceAndBenson, versionedAddressBook);
    }

    @Test
    public void redo_noUndoneState_throwsIllegalStateException() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit();

        assertFalse(versionedAddressBook.canRedo());
        assertThrows(IllegalStateException.class, versionedAddressBook::redo);
    }

    @Test
    public void commit_dataUnchangedAfterUndo_keepsUndoneState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit();
        versionedAddressBook.undo();

        // a command that does not change the data should not clear the redo history
        versionedAddressBook.commit();

        assertTrue(versionedAddressBook.canRedo());
    }

    @Test
    public void commit_afterUndo_discardsUndoneState() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);
        versionedAddressBook.undo();

        versionedAddressBook.addPerson(CARL);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);
        versionedAddressBook.commit();
        assertFalse(versionedAddressBook.canRedo());
        versionedAddressBook.undo();

        // goes back to the state before CARL was added, not the undone state with BENSON
        assertEquals(addressBookWithAlice, versionedAddressBook);
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void commit_dataChangedAfterCommit_keepsSavedStateUnchanged() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBookWithAlice);
        versionedAddressBook.addPerson(BENSON);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);

        // changes made after a commit should not affect the saved state
        versionedAddressBook.addPerson(CARL);
        versionedAddressBook.commit(DEFAULT_COMPARATOR);
        versionedAddressBook.undo();

        assertEquals(addressBookWithAliceAndBenson, versionedAddressBook);
    }
}
