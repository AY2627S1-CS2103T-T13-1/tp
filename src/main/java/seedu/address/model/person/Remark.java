package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represent a Remark tied to a person in the address book.
 */
public class Remark {
    public final String value;

    /**
     * Constructs a new remark.
     * @param value the string value associated with the remark.
     */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Remark other) {
            return value.equals(other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
