package seedu.address.model.person;

public enum SortCriteria {
    NAME("Name"),
    EMAIL("Email"),
    PHONE("Phone");

    private final String displayName;

    SortCriteria(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}