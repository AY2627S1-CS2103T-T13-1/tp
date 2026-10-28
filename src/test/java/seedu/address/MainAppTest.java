package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonUserPrefsStorage;

/**
 * Unit tests for preference initialization and UI delegation, with external dependencies stubbed.
 */
public class MainAppTest {

    private final MainApp mainApp = new MainApp();
    private final UserPrefsStorageStub storage = new UserPrefsStorageStub();

    @Test
    public void initPrefs_storedPrefs_returnsAndSavesStoredPrefs() {
        UserPrefs expected = new UserPrefs();
        expected.setGuiSettings(new GuiSettings(800, 600, 10, 20));
        storage.prefsToRead = Optional.of(expected);

        assertEquals(expected, mainApp.initPrefs(storage));
        assertEquals(expected, storage.savedPrefs);
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void initPrefs_missingFile_returnsAndSavesDefaults() {
        UserPrefs expected = new UserPrefs();

        assertEquals(expected, mainApp.initPrefs(storage));
        assertEquals(expected, storage.savedPrefs);
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void initPrefs_loadingFailure_returnsAndSavesDefaults() {
        storage.readException = new DataLoadingException(new IOException("Cannot read preferences"));
        UserPrefs expected = new UserPrefs();

        assertEquals(expected, mainApp.initPrefs(storage));
        assertEquals(expected, storage.savedPrefs);
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void initPrefs_saveFailure_returnsLoadedPrefs() {
        UserPrefs expected = new UserPrefs();
        expected.setGuiSettings(new GuiSettings(800, 600, 10, 20));
        storage.prefsToRead = Optional.of(expected);
        storage.saveException = new IOException("Cannot save preferences");

        assertEquals(expected, mainApp.initPrefs(storage));
        assertEquals(expected, storage.savedPrefs);
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void start_delegatesToUi() {
        int[] startCalls = {0};
        mainApp.ui = stage -> {
            assertNull(stage);
            startCalls[0]++;
        };

        // A UI stub needs no real Stage or JavaFX application thread.
        mainApp.start(null);

        assertEquals(1, startCalls[0]);
    }

    /**
     * Supplies read outcomes and records save attempts without accessing the file system.
     */
    private static class UserPrefsStorageStub extends JsonUserPrefsStorage {
        private Optional<UserPrefs> prefsToRead = Optional.empty();
        private DataLoadingException readException;
        private IOException saveException;
        private ReadOnlyUserPrefs savedPrefs;
        private int saveAttempts;

        UserPrefsStorageStub() {
            super(Path.of("unused-preferences.json"));
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
            if (readException != null) {
                throw readException;
            }
            return prefsToRead;
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            saveAttempts++;
            savedPrefs = userPrefs;
            if (saveException != null) {
                throw saveException;
            }
        }
    }
}
