package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person matches every supplied criterion using case-insensitive substring matching.
 * An omitted criterion places no restriction on the person.
 */
public class PersonMatchesCriteriaPredicate implements Predicate<Person> {
    private final Optional<String> name;
    private final Optional<String> phone;
    private final Optional<String> email;
    private final Optional<String> address;

    /**
     * Creates a predicate from validated optional search values.
     */
    public PersonMatchesCriteriaPredicate(Optional<String> name, Optional<String> phone, Optional<String> email,
            Optional<String> address) {
        requireAllNonNull(name, phone, email, address);
        this.name = name.map(value -> normalizeName(value).toLowerCase(Locale.ROOT));
        this.phone = phone;
        this.email = email.map(value -> value.toLowerCase(Locale.ROOT));
        this.address = address.map(value -> value.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean test(Person person) {
        return matches(normalizeName(person.getName().fullName), name)
                && matches(person.getPhone().value, phone)
                && matches(person.getEmail().value, email)
                && matches(person.getAddress().value, address);
    }

    private static boolean matches(String value, Optional<String> criterion) {
        return criterion.map(search -> containsIgnoreCase(value, search)).orElse(true);
    }

    private static boolean containsIgnoreCase(String value, String search) {
        return value.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private static String normalizeName(String value) {
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
                && address.equals(otherPredicate.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, address);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .toString();
    }
}
