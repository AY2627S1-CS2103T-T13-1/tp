package seedu.address.model.tag;

/**
 * Represents a Module Tag within the address book.
 */
public class ModuleTag extends Tag {
    public ModuleTag(String tagName) {
        super(tagName);
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
