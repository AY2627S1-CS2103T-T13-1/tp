package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;

/**
 * Creates comparators to sort Person objects in the Address Book.
 */
public class PersonComparatorFactory {

    /**
     * Creates a {@link Comparator} that compares {@link Person} based on {@link SortCriteria}.
     *
     * @param sortCriteria value in {@link Person} to compare by.
     * @param sortOrder order of sorting (ascending/descending).
     */
    public static Comparator<Person> createComparator(SortCriteria sortCriteria, SortOrder sortOrder) {
        requireAllNonNull(sortCriteria, sortOrder);

        Comparator<Person> criteriaComparator = switch (sortCriteria) {
            case NAME -> Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case EMAIL -> Comparator.comparing(person -> person.getEmail().value, String.CASE_INSENSITIVE_ORDER);
            case PHONE -> Comparator.comparing(person -> person.getPhone().value);
        };

        if (sortOrder == SortOrder.DESC) {
            criteriaComparator = criteriaComparator.reversed();
        }

        return createDefaultComparator().thenComparing(criteriaComparator);
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
