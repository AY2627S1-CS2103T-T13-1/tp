package seedu.address.model.tag;

/**
 * Represents a Module Tag within the address book.
 */
public class ModuleTag extends Tag {

    /**
     * Constructs a {@code ModuleTag}.
     *
     * @param tagName a valid tag name.
     */
    public ModuleTag(String tagName) {
        super(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagType() {
        return "module";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ModuleTag moduleTag) {
            return super.equals(moduleTag);
        } else {
            return false;
        }
    }
}
