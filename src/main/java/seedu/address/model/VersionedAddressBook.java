package seedu.address.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparatorFactory;

/**
 * An {@code AddressBook} that keeps a history of its saved states so that changes can be undone and redone.
 */
public class VersionedAddressBook extends AddressBook {

    private final List<SavedState> addressBookStateList = new ArrayList<>();
    private int currentStatePointer;

    /**
     * Creates a {@code VersionedAddressBook} with {@code initialState} as its only saved state.
     */
    public VersionedAddressBook(ReadOnlyAddressBook initialState) {
        super(initialState);
        SavedState initialSavedState = new SavedState(new AddressBook(initialState),
                PersonComparatorFactory.createDefaultComparator());
        addressBookStateList.add(initialSavedState);
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current data as a new state, unless it is the same as the current saved state.
     * When a new state is saved, states after the current state are discarded.
     *
     * @return true if a new state was saved.
     */
    public boolean commit(Comparator<Person> activeComparator) {
        SavedState currentState = addressBookStateList.get(currentStatePointer);
        boolean isSameAddressBook = equals(currentState.readOnlyAddressBook());
        boolean isSameComparator = activeComparator.equals(currentState.comparator());
        if (isSameAddressBook && isSameComparator) {
            return false;
        }

        addressBookStateList.subList(currentStatePointer + 1, addressBookStateList.size()).clear();
        addressBookStateList.add(new SavedState(new AddressBook(this), activeComparator));
        currentStatePointer++;
        assert currentStatePointer == addressBookStateList.size() - 1 : "New state must be the latest";
        return true;
    }

    /**
     * Returns true if there is a previous state to undo to.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Restores the data to the previous saved state.
     *
     * @throws IllegalStateException if there is no previous state.
     */
    public Comparator<Person> undo() {
        if (!canUndo()) {
            throw new IllegalStateException("There is no previous state to undo to.");
        }

        currentStatePointer--;
        assert currentStatePointer >= 0 : "Undo must not move before the first state";
        SavedState previousState = addressBookStateList.get(currentStatePointer);
        resetData(previousState.readOnlyAddressBook());
        return previousState.comparator();
    }

    /**
     * Returns true if there is an undone state to redo to.
     */
    public boolean canRedo() {
        return currentStatePointer < addressBookStateList.size() - 1;
    }

    /**
     * Restores the data to the most recently undone state.
     *
     * @throws IllegalStateException if there is no undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("There is no undone state to redo to.");
        }

        currentStatePointer++;
        assert currentStatePointer < addressBookStateList.size() : "Redo must not move past the last state";
        resetData(addressBookStateList.get(currentStatePointer));
    }
}
