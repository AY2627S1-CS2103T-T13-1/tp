package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.alias.AliasRegistry;

/**
 * Contains validated aliases and warnings for entries skipped during loading.
 *
 * @param aliases the registry containing valid alias mappings.
 * @param warnings the warnings for skipped entries.
 */
public record AliasLoadResult(AliasRegistry aliases, List<String> warnings) {

    /**
     * Constructs a loading result with an immutable copy of warnings.
     */
    public AliasLoadResult {
        requireNonNull(aliases);
        warnings = List.copyOf(warnings);
    }

    /**
     * Returns the number of successfully loaded alias mappings.
     *
     * @return the number of valid mappings in the alias registry.
     */
    public int loadedAliasCount() {
        return aliases.size();
    }
}
