package seedu.address.model.person;

public enum SortOrder {
    ASC("ascending"),
    DESC("descending");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
