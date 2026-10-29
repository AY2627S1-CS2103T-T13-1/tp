package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonComparatorFactoryTest {

    @Test
    public void createComparator_sameCriteriaAndOrder_returnsEqualComparators() {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Comparator<Person> first = PersonComparatorFactory.createComparator(criteria, order);
                Comparator<Person> second = PersonComparatorFactory.createComparator(criteria, order);
                Comparator<Person> third = PersonComparatorFactory.createComparator(criteria, order);

                assertNotSame(first, second);
                assertEquals(first, first);
                assertEquals(first, second);
                assertEquals(second, first);
                assertEquals(second, third);
                assertEquals(first, third);
                assertEquals(first.hashCode(), second.hashCode());
            }
        }
    }

    @Test
    public void createComparator_differentSortSettings_returnsUnequalComparators() {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Comparator<Person> comparator = PersonComparatorFactory.createComparator(criteria, order);
                assertNotEquals(comparator, null);
                assertNotEquals(comparator, criteria);
                assertNotEquals(comparator, PersonComparatorFactory.createDefaultComparator());
                assertNotEquals(PersonComparatorFactory.createDefaultComparator(), comparator);
                for (SortCriteria otherCriteria : SortCriteria.values()) {
                    for (SortOrder otherOrder : SortOrder.values()) {
                        if (criteria != otherCriteria || order != otherOrder) {
                            assertNotEquals(comparator,
                                    PersonComparatorFactory.createComparator(otherCriteria, otherOrder));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void createDefaultComparator_multipleCalls_returnsEqualComparators() {
        Comparator<Person> first = PersonComparatorFactory.createDefaultComparator();
        Comparator<Person> second = PersonComparatorFactory.createDefaultComparator();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void createComparator_allCriteriaAndOrders_sortsWithinPinGroups() {
        Person alice = new PersonBuilder().withName("Alice").withEmail("z@example.com")
                .withPhone("200").build();
        Person bob = new PersonBuilder().withName("Bob").withEmail("a@example.com")
                .withPhone("300").build();
        Person charlie = new PersonBuilder().withName("Charlie").withEmail("m@example.com")
                .withPhone("100").build();

        for (SortCriteria criteria : SortCriteria.values()) {
            List<Person> ascending = switch (criteria) {
                case NAME -> List.of(alice, bob, charlie);
                case EMAIL -> List.of(bob, charlie, alice);
                case PHONE -> List.of(charlie, alice, bob);
            };
            for (SortOrder order : SortOrder.values()) {
                List<Person> ordered = new ArrayList<>(ascending);
                if (order == SortOrder.DESC) {
                    Collections.reverse(ordered);
                }
                List<Person> expected = new ArrayList<>();
                ordered.forEach(person -> expected.add(person.pinPerson()));
                expected.addAll(ordered);
                List<Person> actual = new ArrayList<>(List.of(bob, charlie.pinPerson(), alice,
                        bob.pinPerson(), charlie, alice.pinPerson()));
                actual.sort(PersonComparatorFactory.createComparator(criteria, order));
                assertEquals(expected, actual, criteria + " " + order);
            }
        }
    }

    @Test
    public void createComparator_mixedCaseNamesAndEmails_sortsIgnoringCaseWithinPinGroups() {
        Person alice = new PersonBuilder().withName("alice").withEmail("Zoe@EXAMPLE.com").build();
        Person bob = new PersonBuilder().withName("Bob").withEmail("alice@example.COM").build();
        Person charlie = new PersonBuilder().withName("cHaRlIe").withEmail("Bob@Example.com").build();

        for (SortCriteria criteria : List.of(SortCriteria.NAME, SortCriteria.EMAIL)) {
            List<Person> ascending = criteria == SortCriteria.NAME
                    ? List.of(alice, bob, charlie) : List.of(bob, charlie, alice);
            for (SortOrder order : SortOrder.values()) {
                List<Person> ordered = new ArrayList<>(ascending);
                if (order == SortOrder.DESC) {
                    Collections.reverse(ordered);
                }
                List<Person> expected = new ArrayList<>();
                ordered.forEach(person -> expected.add(person.pinPerson()));
                expected.addAll(ordered);
                List<Person> actual = new ArrayList<>(List.of(charlie, bob.pinPerson(), alice,
                        charlie.pinPerson(), bob, alice.pinPerson()));

                actual.sort(PersonComparatorFactory.createComparator(criteria, order));

                assertEquals(expected, actual, criteria + " " + order);
            }
        }
    }

    @Test
    public void createComparator_keysDifferOnlyInCase_comparesAsEqual() {
        Person lowerCase = new PersonBuilder().withName("alice tan").withEmail("alice@example.com").build();
        Person upperCase = new PersonBuilder().withName("ALICE TAN").withEmail("ALICE@EXAMPLE.COM").build();

        for (SortCriteria criteria : List.of(SortCriteria.NAME, SortCriteria.EMAIL)) {
            for (SortOrder order : SortOrder.values()) {
                Comparator<Person> comparator = PersonComparatorFactory.createComparator(criteria, order);
                assertEquals(0, comparator.compare(lowerCase, upperCase), criteria + " " + order);
                assertEquals(0, comparator.compare(upperCase, lowerCase), criteria + " " + order);
                assertEquals(0, comparator.compare(lowerCase.pinPerson(), upperCase.pinPerson()),
                        criteria + " " + order);
            }
        }
    }

    @Test
    public void createComparator_caseInsensitiveTies_preservesOriginalOrder() {
        Person lowerCase = new PersonBuilder().withName("alice").withEmail("alice@example.com").build();
        Person upperCase = new PersonBuilder().withName("ALICE").withEmail("ALICE@EXAMPLE.COM").build();

        for (SortCriteria criteria : List.of(SortCriteria.NAME, SortCriteria.EMAIL)) {
            for (SortOrder order : SortOrder.values()) {
                Comparator<Person> comparator = PersonComparatorFactory.createComparator(criteria, order);
                for (List<Person> original : List.of(List.of(lowerCase, upperCase), List.of(upperCase, lowerCase))) {
                    List<Person> actual = new ArrayList<>(original);
                    actual.sort(comparator);
                    assertEquals(original, actual, criteria + " " + order);
                }
            }
        }
    }

    @Test
    public void createComparator_equalKeys_returnsZero() {
        Person first = new PersonBuilder().withAddress("First address").build();
        Person second = new PersonBuilder(first).withAddress("Second address").build();
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Comparator<Person> comparator = PersonComparatorFactory.createComparator(criteria, order);
                assertEquals(0, comparator.compare(first, second));
                assertEquals(0, comparator.compare(first, first));
            }
        }
    }

    @Test
    public void createComparator_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                PersonComparatorFactory.createComparator(null, SortOrder.ASC));
        assertThrows(NullPointerException.class, () ->
                PersonComparatorFactory.createComparator(SortCriteria.NAME, null));
    }

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
