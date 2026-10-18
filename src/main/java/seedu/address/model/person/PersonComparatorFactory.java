package seedu.address.model.person;

import java.util.Comparator;
import seedu.address.model.person.Person;

public class PersonComparatorFactory {
    /**public static Comparator<Person> create(String comparisonType, String order) {

    }**/


    /**
     * Creates a {@link Comparator} that prioritizes persons that are pinned.
     */
    public static Comparator<Person> create() {
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
