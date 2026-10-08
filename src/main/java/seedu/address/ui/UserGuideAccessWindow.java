package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a User Guide Access page
 */
public class UserGuideAccessWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2627s1-cs2103t-t13-1.github.io/tp/UserGuide.html";
    public static final String USERGUIDE_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(UserGuideAccessWindow.class);
    private static final String FXML = "UserGuideAccessWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label userGuideMessage;

    /**
     * Creates a new UserGuideAccessWindow.
     *
     * @param root Stage to use as the root of the UserGuideAccessWindow.
     */
    public UserGuideAccessWindow(Stage root) {
        super(FXML, root);
        userGuideMessage.setText(USERGUIDE_MESSAGE);
    }

    /**
     * Creates a new UserGuideAccessWindow.
     */
    public UserGuideAccessWindow() {
        this(new Stage());
    }

    /**
     * Shows the access window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing the user guide link for the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the access window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the access window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the access window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
