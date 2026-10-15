package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests whether a person matches every supplied criterion using case-insensitive substring matching.
 * An omitted criterion places no restriction on the person.
 */
public class PersonMatchesCriteriaPredicate implements Predicate<Person> {
    private final Optional<String> name;
    private final Optional<String> phone;
    private final Optional<String> email;
    private final Optional<String> address;
    private final Set<Tag> tagList;

    /**
     * Creates a predicate from validated optional search values.
     * The name criterion must have its whitespace normalized to single spaces.
     */
    public PersonMatchesCriteriaPredicate(Optional<String> name, Optional<String> phone, Optional<String> email,
            Optional<String> address, Set<Tag> tagList) {
        requireAllNonNull(name, phone, email, address, tagList);
        this.name = name.map(value -> value.toLowerCase(Locale.ROOT));
        this.phone = phone;
        this.email = email.map(value -> value.toLowerCase(Locale.ROOT));
        this.address = address.map(value -> normalizeAddress(value).toLowerCase(Locale.ROOT));
        this.tagList = Set.copyOf(tagList);
    }

    @Override
    public boolean test(Person person) {
        return matchesCriterion(normalizeCriteria(person.getName().fullName), name)
                && matchesCriterion(person.getPhone().value, phone)
                && matchesCriterion(person.getEmail().value, email)
                && matchesCriterion(normalizeAddress(person.getAddress().value), address)
                && matchesAllTags(person, tagList);
    }

    private static boolean matchesCriterion(String value, Optional<String> criterion) {
        return criterion.map(search -> containsCaseInsensitive(value, search)).orElse(true);
    }

    private static boolean matchesAllTags(Person person, Set<Tag> tagList) {
        return tagList.stream()
                .allMatch(criterion ->
                        person.getTags().stream().anyMatch(personTag ->
                                personTag.getTagCategory()
                                        .equals(criterion.getTagCategory())
                                && containsCaseInsensitive(
                                        personTag.tagName, criterion.tagName)));
    }

    private static boolean containsCaseInsensitive(String value, String search) {
        return value.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private static String normalizeCriteria(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    /**
     * Normalizes address whitespace, removing spaces before commas and adding one space after each comma.
     */
    private static String normalizeAddress(String value) {
        return normalizeCriteria(value.replaceAll("\\s*,\\s*", ", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonMatchesCriteriaPredicate otherPredicate)) {
            return false;
        }
        return name.equals(otherPredicate.name)
                && phone.equals(otherPredicate.phone)
                && email.equals(otherPredicate.email)
                && address.equals(otherPredicate.address)
                && tagList.equals(otherPredicate.tagList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, address, tagList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tagList", tagList)
                .toString();
    }
}
