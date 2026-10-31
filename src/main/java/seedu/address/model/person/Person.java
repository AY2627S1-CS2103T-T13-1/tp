package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final boolean isPinned;

    /**
     * Every field must be present and not null.
     * Creates a person that is not pinned
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, false);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, boolean isPinned) {
        requireAllNonNull(name, phone, email, address, tags, isPinned);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        checkArgument(tags.size() <= Tag.MAX_TAGS_PER_PERSON, Tag.MESSAGE_TOO_MANY_TAGS);
        this.tags.addAll(tags);
        this.isPinned = isPinned;
    }

    private Person(Person otherPerson, boolean isPinned) {
        this(otherPerson.name, otherPerson.phone, otherPerson.email, otherPerson.address, otherPerson.tags, isPinned);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isPinned() {
        return isPinned;
    }

    /**
     * Creates a copy of the person with {@code isPinned} set to true.
     */
    public Person pinPerson() {
        return new Person(this, true);
    }

    /**
     * Creates a copy of the person with {@code isPinned} set to false.
     */
    public Person unpinPerson() {
        return new Person(this, false);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same phone number or email
     * address (either one). This defines a weaker notion of equality
     * between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (otherPerson.getPhone().equals(getPhone())
                || otherPerson.getEmail().equals(getEmail()));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     * Tag names are compared case-sensitively so changes to their displayed and stored casing are detected.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && hasSameTagData(otherPerson)
                && isPinned == otherPerson.isPinned;
    }

    /**
     * Compares exact tag data independently of case-insensitive tag identity and set iteration order.
     */
    private boolean hasSameTagData(Person otherPerson) {
        return tags.size() == otherPerson.tags.size()
                && tags.stream().allMatch(tag -> otherPerson.tags.stream().anyMatch(otherTag ->
                        tag.getTagCategory().equals(otherTag.getTagCategory())
                                && tag.tagName.equals(otherTag.tagName)));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, isPinned);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
