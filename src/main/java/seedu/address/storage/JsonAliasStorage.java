package seedu.address.storage;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.alias.AliasRegistry;

/**
 * Provides access to aliases stored as a JSON file on disk.
 */
public class JsonAliasStorage {

    private Path filePath;
    private Set<String> builtInCommandWords;

    /**
     * Creates alias storage using the supplied {@code Path} and supported
     * command words.
     * @param filePath the location of the alias JSON file.
     * @param builtInCommandWords the supported built-in command words used
     *                            to validate loaded aliases.
     */
    public JsonAliasStorage(Path filePath, Set<String> builtInCommandWords) {
        requireAllNonNull(filePath, builtInCommandWords);
        this.filePath = filePath;
        this.builtInCommandWords = Set.copyOf(builtInCommandWords);
    }

    /**
     * Returns the file path of the alias data file.
     */
    public Path getAliasPath() {
        return filePath;
    }

    /**
     * Reads aliases from the configured JSON file and validates all entries.
     *
     * @return the validated alias registry or an empty optional if the file
     *         does not exist.
     * @throws DataLoadingException if the file cannot be read or deserialized.
     */
    public Optional<AliasRegistry> readAliases() throws DataLoadingException {
        Optional<JsonSerializableAliases> jsonAliases = JsonUtil.readJsonFileStrict(
                filePath, JsonSerializableAliases.class);

        if (jsonAliases.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jsonAliases.get().toModelType(builtInCommandWords));
    }

    /**
     * Saves the aliases in the given registry to the configured JSON file.
     *
     * @param aliases the registry of aliases to save.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAliases(AliasRegistry aliases) throws IOException {
        requireNonNull(aliases);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(
                new JsonSerializableAliases(aliases.getAliases()), filePath);
    }
}
