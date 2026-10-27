package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonAliasStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests preference persistence across MainApp's lifecycle with real JSON storage and a model.
 */
public class MainAppIntegrationTest {

    @TempDir
    public Path testFolder;

    private MainApp mainApp;
    private StorageManager storage;
    private JsonUserPrefsStorage prefsStorage;
    private UserPrefs userPrefs;

    @BeforeEach
    public void setUp() {
        mainApp = new MainApp();
        prefsStorage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
        storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("addressbook.json")),
                prefsStorage, new JsonAliasStorage(testFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS));
        userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(800, 600, 10, 20));
    }

    @Test
    public void stop_changedPrefs_persistsPrefsForNextStartup() throws Exception {
        storage.saveUserPrefs(userPrefs);
        mainApp.storage = storage;
        mainApp.model = new ModelManager(new AddressBook(), mainApp.initPrefs(prefsStorage), BUILT_IN_COMMAND_WORDS);
        GuiSettings changedSettings = new GuiSettings(1000, 700, 30, 40);
        mainApp.model.setGuiSettings(changedSettings);

        mainApp.stop();

        MainApp restartedApp = new MainApp();
        UserPrefs reloadedPrefs = restartedApp.initPrefs(prefsStorage);
        assertEquals(changedSettings, reloadedPrefs.getGuiSettings());
        assertEquals(changedSettings, storage.readUserPrefs().orElseThrow().getGuiSettings());
    }

    @Test
    public void stop_saveFailure_doesNotThrowOrOverwriteStoredPrefs() throws Exception {
        // Inject a deterministic write failure rather than relying on OS file permissions.
        JsonUserPrefsStorage failingPrefsStorage = new JsonUserPrefsStorage(prefsStorage.getUserPrefsFilePath()) {
            @Override
            public void saveUserPrefs(ReadOnlyUserPrefs prefs) throws IOException {
                throw new IOException("Cannot save preferences");
            }
        };
        storage.saveUserPrefs(userPrefs);
        mainApp.model = new ModelManager(new AddressBook(), userPrefs, BUILT_IN_COMMAND_WORDS);
        mainApp.model.setGuiSettings(new GuiSettings(1000, 700, 30, 40));
        mainApp.storage = new StorageManager(new JsonAddressBookStorage(storage.getAddressBookFilePath()),
                failingPrefsStorage, new JsonAliasStorage(storage.getAliasPath(), BUILT_IN_COMMAND_WORDS));

        assertDoesNotThrow(mainApp::stop);

        assertEquals(userPrefs, storage.readUserPrefs().orElseThrow());
    }
}
