package seedu.address.model.tag;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Creates {@link Tag} instances of the specified type.
 */
public class TagFactory {

    /**
     * Creates a tag with the given type and name.
     *
     * @param tagType The tag category: {@code module}, {@code faculty}, or {@code others}.
     * @param tagName A valid tag name.
     * @return A tag of the specified category with the given name.
     * @throws ParseException If {@code tagType} is not a supported category.
     * @throws IllegalArgumentException If {@code tagName} is invalid.
     * @throws NullPointerException If {@code tagType} or {@code tagName} is null.
     */
    public static Tag create(String tagType, String tagName) throws ParseException {
        return switch(tagType) {
            case "module" -> new ModuleTag(tagName);
            case "faculty" -> new FacultyTag(tagName);
            case "others" -> new OthersTag(tagName);
            default -> throw new ParseException(Tag.MESSAGE_UNKNOWN_TAG_CATEGORY);
        };
    }
}
