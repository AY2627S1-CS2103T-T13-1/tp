package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should be of the following format:\n"
            + "1. Cannot be blank.\n"
            + "2. Should be 3-15 digits long.\n"
            + "3. can begin with (+<country code>).";
    public static final String VALIDATION_REGEX = "(?:\\(\\+\\d{1,3}\\)\\d{3,15}|\\d{3,15})";
    public static final int MINIMUM_PHONE_LENGTH = 3;
    public static final int MAXIMUM_PHONE_LENGTH = 15;

    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        checkArgument(isValidLength(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public static boolean isValidLength(String test) {
        return test.length() >= MINIMUM_PHONE_LENGTH && test.length() <= MAXIMUM_PHONE_LENGTH;
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
