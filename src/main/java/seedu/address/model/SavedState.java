package seedu.address.model;

import java.util.Comparator;

import seedu.address.model.person.Person;

/**
 * A saved address book state and the comparator used to maintain its sort order.
 */
public record SavedState(ReadOnlyAddressBook readOnlyAddressBook, Comparator<Person> comparator) {
}
