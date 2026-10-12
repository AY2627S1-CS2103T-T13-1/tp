package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is either valid as declared in {@link #isValidAddress(String)}
 * or empty (see {@link #EMPTY}) if no address was provided.
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS = "Addresses can take any values, and should not be blank";
    public static final String MESSAGE_EXCEED_MAX_LENGTH = "Entered address is too long (Should be within 105 characters)"
            ;
    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public static final int MAXIMUM_ADDRESS_LENGTH = 105;

    /** Represents an address that was not provided. */
    public static final Address EMPTY = new Address();

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        checkArgument(isValidLength(address), MESSAGE_EXCEED_MAX_LENGTH);
        value = address;
    }

    /**
     * Constructs an empty {@code Address}. Only used to create {@link #EMPTY}.
     */
    private Address() {
        value = "";
    }

    /**
     * Returns true if a given string is a valid address.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public static boolean isValidLength(String test) { return test.length() <= MAXIMUM_ADDRESS_LENGTH;}
    /**
     * Returns true if this address was not provided.
     */
    public boolean isEmpty() {
        return value.isEmpty();
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
