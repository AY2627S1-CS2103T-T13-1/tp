package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import static seedu.address.testutil.TypicalPersons.ALICE;


import java.util.Comparator;

import org.junit.jupiter.api.Test;

public class PersonComparatorFactoryTest {

    @Test
    public void createDefaultComparator_sortsByPinned() {
        Comparator<Person> comp = PersonComparatorFactory.createDefaultComparator();
        Person pinnedPerson = ALICE.pinPerson();
        Person unpinnedPerson = ALICE;

        assertTrue(comp.compare(pinnedPerson, unpinnedPerson) < 0);
        assertTrue(comp.compare(unpinnedPerson, pinnedPerson) > 0);
        assertTrue(comp.compare(unpinnedPerson, unpinnedPerson) == 0);
        assertTrue(comp.compare(pinnedPerson, pinnedPerson) == 0);

    }
}
