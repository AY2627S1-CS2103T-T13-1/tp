package seedu.address.model.person;

import java.util.Comparator;

/**
 * Creates comparators to sort Person objects in the Address Book
 */
public class PersonComparatorFactory {
    private static Comparator<Person> previousComparator;

    /**
     * Returns the previously created comparator if any.
     * Otherwise, returns the default comparator that prioritizes pinned persons.
     */
    public static Comparator<Person> getPreviousComparator() {
        if (previousComparator != null) {
            return previousComparator;
        }
        Comparator<Person> defaultComparator = createDefaultComparator();
        previousComparator = defaultComparator;
        return defaultComparator;
    }

    /**
     * Creates a {@link Comparator} that prioritizes pinned persons.
     */
    public static Comparator<Person> createDefaultComparator() {
        return (person1, person2) -> {
            if (person1.isPinned() && !person2.isPinned()) {
                return -1;
            } else if (!person1.isPinned() && person2.isPinned()) {
                return 1;
            }
            return 0;
        };
    }
}
