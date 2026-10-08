package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
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
    private final Optional<Tag> tag;

    /**
     * Creates a predicate from validated optional search values.
     */
    public PersonMatchesCriteriaPredicate(Optional<String> name, Optional<String> phone, Optional<String> email,
            Optional<String> address, Optional<Tag> tag) {
        requireAllNonNull(name, phone, email, address, tag);
        this.name = name.map(value -> normalizeCriteria(value).toLowerCase(Locale.ROOT));
        this.phone = phone;
        this.email = email.map(value -> value.toLowerCase(Locale.ROOT));
        this.address = address.map(value -> normalizeCriteria(value).toLowerCase(Locale.ROOT));
        this.tag = tag;
    }

    @Override
    public boolean test(Person person) {
        return matchesCriterion(normalizeCriteria(person.getName().fullName), name)
                && matchesCriterion(person.getPhone().value, phone)
                && matchesCriterion(person.getEmail().value, email)
                && matchesCriterion(normalizeCriteria(person.getAddress().value), address)
                && tag.map(criterion -> person.getTags().stream().anyMatch(personTag ->
                personTag.getTagType().equals(criterion.getTagType())
                                && containsCaseInsensitive(personTag.tagName, criterion.tagName))).orElse(true);
    }

    private static boolean matchesCriterion(String value, Optional<String> criterion) {
        return criterion.map(search -> containsCaseInsensitive(value, search)).orElse(true);
    }

    private static boolean containsCaseInsensitive(String value, String search) {
        return value.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private static String normalizeCriteria(String value) {
        return value.trim().replaceAll("\\s+", " ");
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
                && tag.equals(otherPredicate.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, address, tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tag", tag)
                .toString();
    }
}
