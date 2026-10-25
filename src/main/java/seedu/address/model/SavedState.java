package seedu.address.model.person;

import seedu.address.model.ReadOnlyAddressBook;

public record SavedState(ReadOnlyAddressBook readOnlyAddressBook, SortSettings sortSettings) {
}
