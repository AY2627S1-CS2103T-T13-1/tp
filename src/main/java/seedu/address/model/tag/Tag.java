package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public abstract class Tag {

    public static final String MESSAGE_CONSTRAINTS = "Tag names should be alphanumeric, "
            + "only allowed to have apostrophes and hyphens and should be within 30 characters";
    public static final String MESSAGE_UNKNOWN_TAG_CATEGORY =
            "The provided tag category is currently not supported! Supported tag categories: module, faculty, others";
    public static final String MESSAGE_MISSING_TAG_FIELD = "A valid tag category and tag name is required! "
            + "Format: -t [TAG_CATEGORY] [TAG_NAME]";
    public static final String MESSAGE_TOO_MANY_TAGS = "A contact should not have more than "
            + Tag.MAX_TAGS_PER_PERSON + " tags!";

    public static final String VALIDATION_REGEX = "^[A-Za-z0-9 '-]{1,30}$";

    public static final int MAX_TAGS_PER_PERSON = 5;

    public final String tagName;



    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName;
    }

    /**
     * Returns a string describing a tag's type
     */
    public abstract String getTagType();

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
