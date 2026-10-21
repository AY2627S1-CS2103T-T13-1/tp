package seedu.address.logic.parser;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.model.tag.Tag;

/**
 * Represents the search criteria supplied for filtering contacts.
 *
 * A search criterion may contain a name, phone number, email address,
 * physical address, and one or more tags. Each text-based criterion is
 * optional, while the tag collection is stored as an immutable set.
 */
public class SearchCriteria {
    private final Optional<String> name;
    private final Optional<String> phone;
    private final Optional<String> email;
    private final Optional<String> address;
    private final Set<Tag> tags;

    /**
     * Creates a set of search criteria.
     *
     * @param name the optional name criterion
     * @param phone the optional phone number criterion
     * @param email the optional email address criterion
     * @param address the optional address criterion
     * @param tags the tag criteria used to filter contacts
     */
    public SearchCriteria(Optional<String> name,
                          Optional<String> phone,
                          Optional<String> email,
                          Optional<String> address,
                          Set<Tag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags = Set.copyOf(tags);
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<String> getPhone() {
        return phone;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<String> getAddress() {
        return address;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Creates a predicate that matches persons against these search criteria.
     *
     * @return a predicate for filtering persons
     */
    public Predicate<Person> toPredicate() {
        return new PersonMatchesCriteriaPredicate(
                name, phone, email, address, tags);
    }
}
