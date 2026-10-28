package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.alias.AliasRegistry;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonAliasStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests preference and alias persistence across MainApp's lifecycle with real JSON storage and a model.
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
        mainApp.logic = new LogicManager(mainApp.model, mainApp.storage);
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
        mainApp.logic = new LogicManager(mainApp.model, mainApp.storage);

        assertDoesNotThrow(mainApp::stop);

        assertEquals(userPrefs, storage.readUserPrefs().orElseThrow());
    }

    @Test
    public void stop_aliasSaveStillFails_stillPersistsPrefs() throws Exception {
        RecoverableAliasStorage aliasStorage = setUpRecoverableAliasStorage();
        storage.saveAliases(mainApp.model.getAliasRegistry());
        aliasStorage.failWrites = true;

        assertThrows(CommandException.class, () -> mainApp.logic.execute("alias find f"));

        GuiSettings changedSettings = new GuiSettings(1000, 700,
                30, 40);
        mainApp.model.setGuiSettings(changedSettings);

        assertDoesNotThrow(mainApp::stop);

        assertEquals(changedSettings,
                storage.readUserPrefs().orElseThrow().getGuiSettings());
        assertEquals(Map.of(),
                storage.readAliases().orElseThrow().aliases().getAliases());
        assertEquals(Map.of("f", "find"), mainApp.model.getAliases());
    }

    @Test
    public void stop_pendingAliasRegistrationAfterStorageRecovers_persistsAliasForNextStartup() throws Exception {
        RecoverableAliasStorage aliasStorage = setUpRecoverableAliasStorage();
        storage.saveAliases(mainApp.model.getAliasRegistry());
        aliasStorage.failWrites = true;

        assertThrows(CommandException.class, () -> mainApp.logic.execute("alias find f"));
        assertEquals(Map.of("f", "find"), mainApp.model.getAliases());
        assertEquals(Map.of(), storage.readAliases().orElseThrow().aliases().getAliases());

        aliasStorage.failWrites = false;
        // Exercise the shutdown fallback without another command or UI save attempt.
        mainApp.stop();

        JsonAliasStorage restartedStorage = new JsonAliasStorage(storage.getAliasPath(), BUILT_IN_COMMAND_WORDS);
        assertEquals(Map.of("f", "find"), restartedStorage.readAliases().orElseThrow().aliases().getAliases());
    }

    @Test
    public void stop_pendingAliasDeletionAfterStorageRecovers_keepsAliasDeletedForNextStartup() throws Exception {
        RecoverableAliasStorage aliasStorage = setUpRecoverableAliasStorage();
        mainApp.model.registerAlias("find", "f");
        storage.saveAliases(mainApp.model.getAliasRegistry());
        aliasStorage.failWrites = true;

        assertThrows(CommandException.class, () -> mainApp.logic.execute("delete_alias f"));
        assertEquals(Map.of(), mainApp.model.getAliases());
        assertEquals(Map.of("f", "find"), storage.readAliases().orElseThrow().aliases().getAliases());

        aliasStorage.failWrites = false;
        mainApp.stop();

        JsonAliasStorage restartedStorage = new JsonAliasStorage(storage.getAliasPath(), BUILT_IN_COMMAND_WORDS);
        assertEquals(Map.of(), restartedStorage.readAliases().orElseThrow().aliases().getAliases());
    }

    /**
     * Wires the real logic and model to storage whose alias writes can temporarily fail.
     */
    private RecoverableAliasStorage setUpRecoverableAliasStorage() {
        RecoverableAliasStorage aliasStorage = new RecoverableAliasStorage(testFolder.resolve("aliases.json"));
        storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("addressbook.json")),
                prefsStorage, aliasStorage);
        mainApp.storage = storage;
        mainApp.model = new ModelManager(new AddressBook(), userPrefs, BUILT_IN_COMMAND_WORDS);
        mainApp.logic = new LogicManager(mainApp.model, mainApp.storage);
        return aliasStorage;
    }

    /**
     * Simulates a recoverable write failure while retaining real JSON reads and successful writes.
     */
    private static class RecoverableAliasStorage extends JsonAliasStorage {
        private boolean failWrites;

        RecoverableAliasStorage(Path path) {
            super(path, BUILT_IN_COMMAND_WORDS);
        }

        @Override
        public void saveAliases(AliasRegistry aliases) throws IOException {
            if (failWrites) {
                throw new IOException("Alias storage is temporarily unavailable");
            }
            super.saveAliases(aliases);
        }
    }
}
