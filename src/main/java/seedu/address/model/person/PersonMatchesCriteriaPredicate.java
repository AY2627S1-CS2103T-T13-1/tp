package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's name contains the supplied phrase, ignoring case.
 * An omitted criterion places no restriction on the person.
 */
public class PersonMatchesCriteriaPredicate implements Predicate<Person> {
    private final Optional<String> name;

    /**
     * Creates a predicate from validated optional search values.
     */
    public PersonMatchesCriteriaPredicate(Optional<String> name) {
        requireAllNonNull(name);
        this.name = name.map(value -> normalizeName(value).toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean test(Person person) {
        return name.map(search -> containsIgnoreCase(normalizeName(person.getName().fullName), search)).orElse(true);
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
        return name.equals(otherPredicate.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .toString();
    }
}
