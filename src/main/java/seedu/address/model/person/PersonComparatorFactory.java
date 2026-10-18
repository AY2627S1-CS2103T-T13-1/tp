package seedu.address.model.person;

import java.util.Comparator;

public class PersonComparatorFactory {
    private static Comparator<Person> PREVIOUS_COMPARATOR;

    /**
     * Returns the previously created comparator if any.
     * Otherwise, returns the default comparator that prioritizes pinned persons.
     */
    public static Comparator<Person> getPreviousComparator() {
        if (PREVIOUS_COMPARATOR != null) {
            return PREVIOUS_COMPARATOR;
        }
        Comparator<Person> defaultComparator = createDefaultComparator();
        PREVIOUS_COMPARATOR = defaultComparator;
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
