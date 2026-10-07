package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represent a Remark tied to a person in the address book.
 */
public class Remark {

    public static final String MESSAGE_CONSTRAINTS =
            "Remarks should only contain alphanumeric characters and spaces and can be blank";
    public static final String VALIDATION_REGEX = "(?:[\\p{Alnum}][\\p{Alnum} .:]*)?";

    public final String value;

    /**
     * Constructs a new remark.
     * @param value the string value associated with the remark.
     */
    public Remark(String value) {
        requireNonNull(value);
        checkArgument(isValidRemark(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    public static boolean isValidRemark(String test) {
        return test.matches(VALIDATION_REGEX);
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
