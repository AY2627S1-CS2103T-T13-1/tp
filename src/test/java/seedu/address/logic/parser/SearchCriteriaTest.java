package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;

public class SearchCriteriaTest {

    @Test
    public void constructor_validCriteria_storesAllValues() {
        Set<Tag> tags = Set.of(new OthersTag("friends"));
        SearchCriteria criteria = new SearchCriteria(
                Optional.of("Alice"),
                Optional.of("94351253"),
                Optional.of("alice@example.com"),
                Optional.of("123, Jurong West Ave 6, #08-111"),
                tags);

        assertEquals(Optional.of("Alice"), criteria.getName());
        assertEquals(Optional.of("94351253"), criteria.getPhone());
        assertEquals(Optional.of("alice@example.com"), criteria.getEmail());
        assertEquals(Optional.of("123, Jurong West Ave 6, #08-111"), criteria.getAddress());
        assertEquals(tags, criteria.getTags());
    }

    @Test
    public void constructor_mutableTagSet_copiesTags() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new OthersTag("friends"));

        SearchCriteria criteria = new SearchCriteria(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), tags);

        tags.add(new OthersTag("colleagues"));

        assertEquals(Set.of(new OthersTag("friends")), criteria.getTags());
    }

    @Test
    public void getTags_returnedSetIsImmutable() {
        SearchCriteria criteria = new SearchCriteria(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Set.of(new OthersTag("friends")));

        assertThrows(UnsupportedOperationException.class, ()
                -> criteria.getTags().add(new OthersTag("colleagues")));
    }

    @Test
    public void toPredicate_matchingCriteria_matchesMatchingPerson() {
        SearchCriteria criteria = new SearchCriteria(
                Optional.of("Alice"),
                Optional.of("94351253"),
                Optional.of("alice@example.com"),
                Optional.of("Jurong West"),
                Set.of(new OthersTag("friends")));

        Predicate<Person> predicate = criteria.toPredicate();

        assertTrue(predicate.test(ALICE));
        assertFalse(predicate.test(BENSON));
    }

    @Test
    public void toPredicate_emptyCriteria_matchesEveryPerson() {
        SearchCriteria criteria = new SearchCriteria(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Set.of());

        Predicate<Person> predicate = criteria.toPredicate();

        assertTrue(predicate.test(ALICE));
        assertTrue(predicate.test(CARL));
    }
}
