package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.FacultyTag;
import seedu.address.model.tag.ModuleTag;
import seedu.address.model.tag.OthersTag;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the matching rules of {@code PersonMatchesCriteriaPredicate} directly.
 */
public class PersonMatchesCriteriaPredicateTest {

    @Test
    public void test_nameCriterion_matchesContiguousSubstringIgnoringCase() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("WOLF ALI"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of());

        assertTrue(predicate.test(new PersonBuilder().withName("Wolf Alice").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Wolf   Alice").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Wolf").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Wolf Bob Alice").build()));
    }

    @Test
    public void test_phoneCriterion_matchesSubstring() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.of("765"),
                Optional.empty(),
                Optional.empty(),
                Set.of());

        assertTrue(predicate.test(BENSON));
        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_emailCriterion_matchesSubstringIgnoringCase() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.of("JOHN"),
                Optional.empty(),
                Set.of());

        assertTrue(predicate.test(BENSON));
        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_addressCriterion_normalizesWhitespaceAndMatchesSubstringIgnoringCase() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("  BLOCK\t ABC    ,   "),
                Set.of());

        assertTrue(predicate.test(new PersonBuilder().withAddress("123 Block   ABC, Hougang").build()));
        assertFalse(predicate.test(new PersonBuilder().withAddress("123 Block DEF, Hougang").build()));
        assertFalse(predicate.test(new PersonBuilder().withoutAddress().build()));
    }

    @Test
    public void test_multipleCriteria_requiresEveryCriterionToMatch() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("BEN"),
                Optional.of("987"),
                Optional.of("JOHN"),
                Optional.of("CLEMENTI"),
                Set.of(new OthersTag("friends")));

        assertTrue(predicate.test(BENSON));
        // Each person below fails exactly one supplied criterion.
        assertFalse(predicate.test(new PersonBuilder(BENSON).withName("Alice Pauline").build()));
        assertFalse(predicate.test(new PersonBuilder(BENSON).withPhone("94351253").build()));
        assertFalse(predicate.test(new PersonBuilder(BENSON).withEmail("alice@example.com").build()));
        assertFalse(predicate.test(new PersonBuilder(BENSON).withAddress("123 Jurong West").build()));
        assertFalse(predicate.test(new PersonBuilder(BENSON).withTags(new OthersTag("owesMoney")).build()));
    }

    @Test
    public void test_tagCriterion_matchesSubstringIgnoringCase() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("FRIEND")));

        assertTrue(predicate.test(BENSON)); // Extra person tags do not prevent a match.
        assertTrue(predicate.test(ALICE)); // exactly one others Tag, friends
        assertFalse(predicate.test(new PersonBuilder().withTags(new OthersTag("colleagues")).build()));
        assertFalse(predicate.test(CARL)); // Carl has no tags
    }

    @Test
    public void test_tagNameMatchesButCategoryDiffers_returnsFalse() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new ModuleTag("friends")));

        assertFalse(predicate.test(ALICE)); // tag category others
    }

    @Test
    public void test_multipleTags_requiresAllTags() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("friends"), new OthersTag("owesMoney")));

        assertTrue(predicate.test(BENSON));
        assertFalse(predicate.test(new PersonBuilder().withTags(new OthersTag("owesMoney")).build()));
        assertFalse(predicate.test(CARL));
    }

    @Test
    public void test_multipleTagCategories_requiresAllTags() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("friend"), new FacultyTag("SCI")));

        assertTrue(predicate.test(new PersonBuilder()
                .withTags(new OthersTag("friends"),
                        new FacultyTag("Science"),
                        new ModuleTag("CS2103T")).build()));
        assertFalse(predicate.test(ALICE)); // Alice only has others Tag, friends
    }

    @Test
    public void test_nameAndTagsMatchingDifferentPersons_returnsFalse() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("Alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of(new OthersTag("friends"), new OthersTag("owesMoney")));

        assertFalse(predicate.test(ALICE)); // Name matches, but one required tag is missing.
        assertFalse(predicate.test(BENSON)); // Both tags match, but the name does not.
    }
}
