package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code AddressBook} that keeps a history of its saved states so that changes can be undone.
 */
public class VersionedAddressBook extends AddressBook {

    private final List<ReadOnlyAddressBook> addressBookStateList = new ArrayList<>();
    private int currentStatePointer;

    /**
     * Creates a {@code VersionedAddressBook} with {@code initialState} as its only saved state.
     */
    public VersionedAddressBook(ReadOnlyAddressBook initialState) {
        super(initialState);
        addressBookStateList.add(new AddressBook(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current data as a new state, unless it is the same as the current saved state.
     * Saved states after the current state are discarded.
     *
     * @return true if a new state was saved.
     */
    public boolean commit() {
        if (equals(addressBookStateList.get(currentStatePointer))) {
            return false;
        }

        addressBookStateList.subList(currentStatePointer + 1, addressBookStateList.size()).clear();
        addressBookStateList.add(new AddressBook(this));
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
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("There is no previous state to undo to.");
        }

        currentStatePointer--;
        assert currentStatePointer >= 0 : "Undo must not move before the first state";
        resetData(addressBookStateList.get(currentStatePointer));
    }
}
