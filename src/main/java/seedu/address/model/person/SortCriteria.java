package seedu.address.model.person;

/**
 * Specifies the person field used for sorting contacts.
 */
public enum SortCriteria {
    /** Sorts contacts by name. */
    NAME("Name"),
    /** Sorts contacts by email address. */
    EMAIL("Email"),
    /** Sorts contacts by phone number. */
    PHONE("Phone");

    private final String displayName;

    /**
     * Creates a sort criterion with the given user-facing display name.
     *
     * @param displayName the name used in command feedback.
     */
    SortCriteria(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-facing display name of this sort criterion.
     */
    public String getDisplayName() {
        return displayName;
    }
}
