package seedu.address.storage;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a filename used for exporting an address book.
 *
 * <p>A filename must contain only alphanumeric characters and spaces,
 * must begin with an alphanumeric character, and must not exceed
 * {@value #MAXIMUM_NAME_LENGTH} characters.</p>
 */
public class Filename {

    public static final int MAXIMUM_NAME_LENGTH = 100;
    public static final String MESSAGE_CONSTRAINTS =
            "Filenames should only contain alphanumeric characters and spaces, and should not contain file extensions.";

    public static final String MESSAGE_EXCEED_MAX_LENGTH = "Entered filename is too long. "
            + " (Should be at most 100 characters)";

    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]{0,99}";

    public final String fileName;

    /**
     * Constructs a {@code Filename} with the specified filename.
     *
     * @param fileName the filename to validate and store
     * @throws NullPointerException if {@code fileName} is null
     * @throws IllegalArgumentException if the filename is invalid or too long
     */
    public Filename(String fileName) {
        requireNonNull(fileName);
        checkArgument(isValidLength(fileName), MESSAGE_EXCEED_MAX_LENGTH);
        checkArgument(isValidName(fileName), MESSAGE_CONSTRAINTS);
        this.fileName = fileName;
    }

    /**
     * Checks whether the specified filename contains only valid characters
     * and follows the required filename format.
     *
     * @param test the filename to validate
     * @return {@code true} if the filename is valid; {@code false} otherwise
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Checks whether the specified filename does not exceed the maximum length.
     *
     * @param test the filename to check
     * @return {@code true} if the filename is at most
     *         {@value #MAXIMUM_NAME_LENGTH} characters long; {@code false} otherwise
     */
    public static boolean isValidLength(String test) {
        return test.length() <= MAXIMUM_NAME_LENGTH;
    }

    /**
     * Returns the filename as a string.
     *
     * @return the filename
     */
    @Override
    public String toString() {
        return fileName;
    }

    /**
     * Returns whether this filename is equal to another object.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects represent the same filename;
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Filename otherFile)) {
            return false;
        }

        return fileName.equals(otherFile.fileName);
    }

    /**
     * Returns the hash code of this filename.
     *
     * @return the hash code of the filename
     */
    @Override
    public int hashCode() {
        return fileName.hashCode();
    }
}
