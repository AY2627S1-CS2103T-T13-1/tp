package seedu.address.model.tag;

/**
 * Represents an Others tag in the address book.
 */
public class OthersTag extends Tag {

    /**
     * Constructs an {@code OthersTag}.
     * @param tagName a valid tag name.
     */
    public OthersTag(String tagName) {
        super(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof OthersTag othersTag) {
            return super.equals(othersTag);
        } else {
            return false;
        }
    }
}
