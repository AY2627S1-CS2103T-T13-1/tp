package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Creates comparators to sort Person objects in the Address Book.
 */
public class PersonComparatorFactory {

    private static final Comparator<Person> DEFAULT_COMPARATOR =
            Comparator.comparing(Person::isPinned).reversed();

    /**
     * Creates a {@link Comparator} that compares {@link Person} based on {@link SortCriteria}.
     * Comparators created with the same criteria and order are equal.
     *
     * @param sortCriteria value in {@link Person} to compare by.
     * @param sortOrder order of sorting (ascending/descending).
     */
    public static Comparator<Person> createComparator(SortCriteria sortCriteria, SortOrder sortOrder) {
        requireAllNonNull(sortCriteria, sortOrder);

        Comparator<Person> criteriaComparator = switch (sortCriteria) {
            case NAME -> Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case EMAIL -> Comparator.comparing(person -> person.getEmail().value, String.CASE_INSENSITIVE_ORDER);
            case PHONE -> Comparator.comparing(person -> person.getPhone().convertToLong());
        };

        if (sortOrder == SortOrder.DESC) {
            criteriaComparator = criteriaComparator.reversed();
        }

        return new SortComparator(sortCriteria, sortOrder, DEFAULT_COMPARATOR.thenComparing(criteriaComparator));
    }

    /**
     * Returns the shared default {@link Comparator} that prioritizes pinned persons.
     */
    public static Comparator<Person> createDefaultComparator() {
        return DEFAULT_COMPARATOR;
    }

    /**
     * Compares persons using a delegate, with equality defined by the selected sort criteria and order.
     */
    private static final class SortComparator implements Comparator<Person> {
        private final SortCriteria sortCriteria;
        private final SortOrder sortOrder;
        private final Comparator<Person> delegate;

        private SortComparator(SortCriteria sortCriteria, SortOrder sortOrder, Comparator<Person> delegate) {
            this.sortCriteria = sortCriteria;
            this.sortOrder = sortOrder;
            this.delegate = delegate;
        }

        @Override
        public int compare(Person first, Person second) {
            return delegate.compare(first, second);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof SortComparator otherComparator)) {
                return false;
            }
            return sortCriteria == otherComparator.sortCriteria && sortOrder == otherComparator.sortOrder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sortCriteria, sortOrder);
        }
    }
}
