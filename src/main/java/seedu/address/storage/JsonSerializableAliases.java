package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

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
    public JsonSerializableAliases(Map<String, String> aliases) {
        requireNonNull(aliases);
        this.aliases = new HashMap<>(aliases);
    }

    /**
     * Converts stored aliases into an alias registry.
     * Mappings with invalid alias syntax, unsupported command words, or
     * conflicts with built-in commands are skipped with warnings.
     *
     * @param builtInCommandWords the supported built-in command words.
     * @return an {@code AliasRegistry} containing validated mappings.
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

    /**
     * Creates an alias snapshot from JSON.
     * Entries with non-string command words are skipped with warnings.
     * Alias syntax and supported command words are validated by
     * {@link #toModelType(Set)}.
     *
     * @param aliases the JSON object mapping aliases to command words.
     * @return a snapshot containing only string-valued mappings.
     * @throws IllegalArgumentException if the aliases property is missing,
     *         null, or not a JSON object.
     */
    @JsonCreator
    public static JsonSerializableAliases fromJson(@JsonProperty("aliases") JsonNode aliases)
            throws IllegalArgumentException{
        if (aliases == null || !aliases.isObject()) {
            throw new IllegalArgumentException("The alias must be a valid JSON object.");
        }

        Map<String, String> aliasMappings = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> entries = aliases.fields();

        while (entries.hasNext()) {
            Map.Entry<String, JsonNode> entry = entries.next();
            String alias = entry.getKey();
            JsonNode commandWord = entry.getValue();

            if (!commandWord.isTextual()) {
                logger.warning(String.format(MESSAGE_INVALID_ALIAS_ENTRY, alias,
                        "Command word must be a string."));
                continue;
            }
            aliasMappings.put(alias, commandWord.textValue());
        }
        return new JsonSerializableAliases(aliasMappings);
    }
}
