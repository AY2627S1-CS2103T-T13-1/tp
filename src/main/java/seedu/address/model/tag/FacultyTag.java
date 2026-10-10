package seedu.address.model.tag;

/**
 * Represents a Faculty Tag within the address book.
 */
public class FacultyTag extends Tag {

    /**
     * Constructs a {@code FacultyTag}.
     *
     * @param tagName a valid tag name.
     */
    public FacultyTag(String tagName) {
        super(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagCategory() {
        return "faculty";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FacultyTag facultyTag) {
            return super.equals(facultyTag);
        } else {
            return false;
        }
    }
}
