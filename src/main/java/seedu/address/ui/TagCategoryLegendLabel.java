package seedu.address.ui;

import javafx.scene.control.Label;

/**
 * A label showing a tag category in its corresponding colour.
 */
public class TagCategoryLegendLabel extends Label {

    /**
     * Creates a legend label for the given tag category.
     */
    public TagCategoryLegendLabel(String tagCategory) {
        super(tagCategory);
        getStyleClass().add("tag-" + tagCategory);
    }
}
