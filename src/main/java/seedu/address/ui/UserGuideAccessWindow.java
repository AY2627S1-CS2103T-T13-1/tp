package seedu.address.ui;

import static seedu.address.commons.core.AppConstants.USERGUIDE_URL;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        copyButton.setText(canOpenBrowser() ? "Open User Guide" : "Copy URL");
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
     * Opens the user guide in the default browser if browsing is supported.
     * Otherwise, falls back to copying the user guide URL to the clipboard.
     */
    @FXML
    private void handleUrl() {
        // Solution below adapted from:
        // https://stackoverflow.com/questions/5226212/how-to-open-url-in-default-webbrowser-using-java
        if (canOpenBrowser()) {
            try {
                Desktop.getDesktop().browse(new URI(USERGUIDE_URL));
            } catch (IOException | URISyntaxException e) {
                logger.warning("Unable to open the user guide: " + e.getMessage());
                copyButton.setText("Copy URL");
                copyButton.setOnAction(event -> copyUrl());
                userGuideMessage.setText("Unable to open the user guide in default browser. "
                        + "Please copy the URL instead.");
            }
        } else {
            copyUrl();
        }
    }

    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }

    private static boolean canOpenBrowser() {
        return Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }
}
