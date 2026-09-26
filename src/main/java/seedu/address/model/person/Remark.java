package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's remark in the address book.
 * Guarantees: immutable
 */
public class Remark {

    public static final String MESSAGE_CONSTRAINTS = "Remarks can take any values, and should not be blank";

    public final String value;

    /**
     * Constructs a {@code Remark}.
     *
     * @param remark A valid Remark.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}