package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.alias.AliasRegistry;

/**
 * Snapshot of aliases that is serializable to JSON format.
 */
public class JsonSerializableAliases {

    private static final String MESSAGE_INVALID_ALIAS_ENTRY = "Invalid alias entry '%s': %s";

    private final Map<String, String> aliases;

    /**
     * Constructs a snapshot of the given registry for serialization.
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
     *
     * @param builtInCommandWords the supported built-in command words.
     * @return an {@code AliasRegistry} containing validated aliases.
     * @throws IllegalValueException if any of the stored alias entries are invalid.
     */
    public AliasRegistry toModelType(Set<String> builtInCommandWords)
            throws IllegalValueException {
        requireNonNull(builtInCommandWords);

        AliasRegistry aliasRegistry = new AliasRegistry(builtInCommandWords);

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            String commandWord = entry.getValue();

            if (alias == null || commandWord == null) {
                throw new IllegalValueException("Alias and command words cannot be null!");
            }

            try {
                aliasRegistry.register(commandWord, alias);
            } catch (IllegalArgumentException exception) {
                throw new IllegalValueException(String.format(MESSAGE_INVALID_ALIAS_ENTRY,
                        alias, exception.getMessage()), exception);
            }
        }
        return aliasRegistry;
    }
}
