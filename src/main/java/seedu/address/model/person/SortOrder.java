package seedu.address.model.person;

/**
 * Specifies the direction in which a sort criterion is applied.
 * Pinned contacts remain first regardless of this order.
 */
public enum SortOrder {
    /** Applies the sort criterion in ascending order. */
    ASC("ascending"),
    /** Applies the sort criterion in descending order. */
    DESC("descending");

    private final String displayName;

    /**
     * Creates a sort order with the given user-facing display name.
     *
     * @param displayName the name used in command feedback.
     */
    SortOrder(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-facing display name of this sort order.
     */
    public String getDisplayName() {
        return displayName;
    }
}
