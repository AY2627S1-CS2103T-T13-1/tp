package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.alias.AliasRegistry;

/**
 * Snapshot of aliases that is serializable to JSON format.
 */
public class JsonSerializableAliases {

    private static final String MESSAGE_INVALID_ALIAS_ENTRY = "Invalid alias entry '%s': %s";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAliases.class);

    private final Map<String, String> aliases;

    /**
     * Constructs a snapshot of the given alias mappings for serialization.
     *
     * @param aliases the set of alias and command word mappings.
     */
    @JsonCreator
    public JsonSerializableAliases(@JsonProperty("aliases") Map<String, String> aliases) {
        requireNonNull(aliases);
        this.aliases = new HashMap<>(aliases);
    }

    /**
     * Converts stored aliases into an alias registry.
     * Invalid mappings are skipped with warnings while valid mappings are registered.
     *
     * @param builtInCommandWords the supported built-in command words.
     * @return an {@code AliasRegistry} containing validated aliases.
     */
    public AliasRegistry toModelType(Set<String> builtInCommandWords) {
        requireNonNull(builtInCommandWords);

        AliasRegistry aliasRegistry = new AliasRegistry(builtInCommandWords);

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            String commandWord = entry.getValue();

            if (alias == null || commandWord == null) {
                logger.warning(String.format(MESSAGE_INVALID_ALIAS_ENTRY, alias,
                        "Alias and command words cannot be null!"));
                continue;
            }

            try {
                aliasRegistry.register(commandWord, alias);
            } catch (IllegalArgumentException exception) {
                logger.warning(String.format(MESSAGE_INVALID_ALIAS_ENTRY, alias,
                        exception.getMessage()));
            }
        }
        return aliasRegistry;
    }
}
