package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.alias.AliasRegistry;

public class JsonAliasStorageTest {

    private static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of("find", "list");

    @TempDir
    public Path testFolder;

    @Test
    public void readAliases_missingFile_returnsEmpty() throws DataLoadingException {
        JsonAliasStorage storage = new JsonAliasStorage(testFolder.resolve("missing.json"),
                BUILT_IN_COMMAND_WORDS);

        assertEquals(Optional.empty(), storage.readAliases());
    }

    @Test
    public void saveAndReadAliases_newAndExistingFile_returnsSavedAliases() throws Exception {
        Path filePath = testFolder.resolve("nested").resolve("aliases.json");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);
        AliasRegistry registry = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        registry.register("find", "f");
        registry.register("list", "L");

        assertEquals(filePath, storage.getAliasPath());
        storage.saveAliases(registry);
        assertEquals(Optional.of(registry),
                new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS).readAliases());

        AliasRegistry emptyRegistry = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        storage.saveAliases(emptyRegistry);
        assertEquals(Optional.of(emptyRegistry),
                new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS).readAliases());
    }

    @Test
    public void readAliases_invalidCommandWord_skipsInvalidEntry() throws Exception {
        Path filePath = testFolder.resolve("invalidEntry.json");
        FileUtil.writeToFile(filePath, "{\"aliases\":{\"f\":\"unknown\",\"l\":\"list\"}}");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);

        AliasRegistry expected = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        expected.register("list", "l");

        assertEquals(Optional.of(expected), storage.readAliases());
    }

    @Test
    public void readAliases_nonStringValue_preservesValidEntry() throws Exception {
        Path filePath = testFolder.resolve("nonStringValue.json");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);
        AliasRegistry expected = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        expected.register("list", "L");

        // Exercise each non-string JSON value type independently to avoid masking failures.
        for (String invalidValue : new String[]{"{}", "[]", "null", "true", "123", "1.5"}) {
            FileUtil.writeToFile(filePath, "{\"aliases\":{\"L\":\"list\",\"bad\":"
                    + invalidValue + "}}");

            assertEquals(Optional.of(expected), storage.readAliases(),
                    "Invalid value: " + invalidValue);
        }
    }

    @Test
    public void readAliases_onlyNonStringValue_returnsEmptyRegistry() throws Exception {
        Path filePath = testFolder.resolve("onlyInvalidEntry.json");
        FileUtil.writeToFile(filePath, "{\"aliases\":{\"bad\":{}}}");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);

        assertEquals(Optional.of(new AliasRegistry(BUILT_IN_COMMAND_WORDS)), storage.readAliases());
    }

    @Test
    public void saveAndReadAliases_invalidEntry_preservesExistingAndNewAliases() throws Exception {
        Path filePath = testFolder.resolve("partiallyValidAliases.json");
        FileUtil.writeToFile(filePath, "{\"aliases\":{\"L\":\"list\",\"bad\":{}}}");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);

        AliasRegistry loaded = storage.readAliases().orElseThrow();
        loaded.register("find", "f");
        storage.saveAliases(loaded);

        AliasRegistry expected = new AliasRegistry(BUILT_IN_COMMAND_WORDS);
        expected.register("list", "L");
        expected.register("find", "f");
        assertEquals(Optional.of(expected),
                new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS).readAliases());
    }

    @Test
    public void readAliases_duplicateKeys_throwsDataLoadingException() throws IOException {
        Path filePath = testFolder.resolve("duplicateKeys.json");
        FileUtil.writeToFile(filePath, "{\"aliases\":{\"f\":\"find\",\"f\":\"list\"}}");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);

        assertThrows(DataLoadingException.class, storage::readAliases);
    }

    @Test
    public void saveAliases_directory_throwsIoException() {
        JsonAliasStorage storage = new JsonAliasStorage(testFolder, BUILT_IN_COMMAND_WORDS);

        assertThrows(IOException.class, () ->
                storage.saveAliases(new AliasRegistry(BUILT_IN_COMMAND_WORDS)));
    }

    @Test
    public void readAliases_trailingGarbage_throwsDataLoadingException() throws IOException {
        Path filePath = testFolder.resolve("trailingGarbage.json");
        FileUtil.writeToFile(filePath, "{\"aliases\":{\"f\":\"find\"}} garbage");
        JsonAliasStorage storage = new JsonAliasStorage(filePath, BUILT_IN_COMMAND_WORDS);

        assertThrows(DataLoadingException.class, storage::readAliases);
    }
}
