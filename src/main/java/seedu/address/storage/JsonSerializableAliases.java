package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.alias.AliasRegistry;

/**
 * Snapshot of aliases that is serializable to JSON format.
 */
public class JsonSerializableAliases {

    private static final String MESSAGE_INVALID_ALIAS_ENTRY = "Skipped alias entry '%s': %s";
    private static final String MESSAGE_MISSING_ALIASES_PROPERTY = "Missing required property 'aliases'. "
            + "Expected an object such as {\"aliases\":{\"f\":\"find\"}}.";
    private static final String MESSAGE_NULL_ALIASES_PROPERTY = "Property 'aliases' must be a JSON object, "
            + "not null. Use {\"aliases\":{}} if there are no saved aliases.";
    private static final String MESSAGE_INVALID_ALIASES_PROPERTY_TYPE =
            "Property 'aliases' must be a JSON object, but found %s.";
    private static final String MESSAGE_NON_STRING_COMMAND_WORD =
            "Skipped alias '%s': expected a command word as a JSON string, but found %s.";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAliases.class);

    private final Map<String, String> aliases;

    @JsonIgnore
    private final List<String> loadingWarnings;

    /**
     * Constructs a snapshot of the supplied alias mappings with no loading warnings.
     *
     * @param aliases the mappings from aliases to command words.
     */
    public JsonSerializableAliases(Map<String, String> aliases) {
        this(aliases, List.of());
    }

    /**
     * Constructs a snapshot of alias mappings and warnings collected during deserialization.
     * Loading warnings are excluded from JSON serialization.
     *
     * @param aliases the mappings retained during deserialization.
     * @param loadingWarnings the warnings for skipped entries.
     */
    private JsonSerializableAliases(Map<String, String> aliases, List<String> loadingWarnings) {
        requireNonNull(aliases);
        this.aliases = new HashMap<>(aliases);
        this.loadingWarnings = List.copyOf(loadingWarnings);
    }

    /**
     * Converts stored mappings into an alias registry, skipping invalid entries.
     *
     * @param builtInCommandWords the supported built-in command words.
     * @return an {@code AliasRegistry} containing valid alias mappings.
     */
    public AliasRegistry toModelType(Set<String> builtInCommandWords) {
        return toLoadResult(builtInCommandWords).aliases();
    }


    /**
     * Converts stored mappings into validated aliases and loading warnings.
     * Invalid mappings are logged and skipped while valid mappings are retained.
     *
     * @param builtInCommandWords the supported built-in command words.
     * @return the valid aliases and warnings for all skipped entries.
     */
    public AliasLoadResult toLoadResult(Set<String> builtInCommandWords) {
        requireNonNull(builtInCommandWords);

        AliasRegistry aliasRegistry = new AliasRegistry(builtInCommandWords);
        List<String> warnings = new ArrayList<>(loadingWarnings);

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            String commandWord = entry.getValue();

            if (alias == null || commandWord == null) {
                addWarning(alias, "Alias and command words cannot be null.", warnings);
                continue;
            }

            try {
                aliasRegistry.register(commandWord, alias);
            } catch (IllegalArgumentException exception) {
                addWarning(alias, exception.getMessage(), warnings);
            }
        }
        return new AliasLoadResult(aliasRegistry, warnings);
    }

    /**
     * Creates an alias snapshot from JSON.
     * Entries with non-string command words are skipped with warnings.
     * Alias syntax and supported command words are validated by
     * {@link #toLoadResult(Set)}.
     *
     * @param aliases the JSON object mapping aliases to command words.
     * @return a snapshot containing only string-valued mappings.
     * @throws IllegalArgumentException if the aliases property is missing,
     *         null, or not a JSON object.
     */
    @JsonCreator
    public static JsonSerializableAliases fromJson(@JsonProperty("aliases") JsonNode aliases)
            throws IllegalArgumentException {
        if (aliases == null || aliases.isMissingNode()) {
            throw new IllegalArgumentException(MESSAGE_MISSING_ALIASES_PROPERTY);
        }

        if (aliases.isNull()) {
            throw new IllegalArgumentException(MESSAGE_NULL_ALIASES_PROPERTY);
        }

        if (!aliases.isObject()) {
            throw new IllegalArgumentException(String.format(MESSAGE_INVALID_ALIASES_PROPERTY_TYPE,
                    aliases.getNodeType().name().toLowerCase(Locale.ROOT)));
        }

        Map<String, String> aliasMappings = new HashMap<>();
        List<String> warnings = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> entries = aliases.fields();

        while (entries.hasNext()) {
            Map.Entry<String, JsonNode> entry = entries.next();
            String alias = entry.getKey();
            JsonNode commandWord = entry.getValue();

            if (!commandWord.isTextual()) {
                String warning = String.format(MESSAGE_NON_STRING_COMMAND_WORD, alias,
                        commandWord.getNodeType().name().toLowerCase(Locale.ROOT));

                logger.warning(warning);
                warnings.add(warning);
                continue;
            }
            aliasMappings.put(alias, commandWord.textValue());
        }
        return new JsonSerializableAliases(aliasMappings, warnings);
    }

    private static void addWarning(String alias, String reason, List<String> warnings) {
        String warning = String.format(MESSAGE_INVALID_ALIAS_ENTRY, alias, reason);

        logger.warning(warning);
        warnings.add(warning);
    }
}
