package seedu.address.model.alias;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores the aliases for the current application session.
 */
public class AliasRegistry {
    private final Map<String, String> aliases = new HashMap<>();
    private final Set<String> builtInCommandWords;

    /**
     * Constructs a new Registry of Aliases.
     *
     * @param builtInCommandWords the set of supported command words in coNnectUS.
     */
    public AliasRegistry(Set<String> builtInCommandWords) {
        requireNonNull(builtInCommandWords);
        this.builtInCommandWords = Set.copyOf(builtInCommandWords);
    }

    /**
     * Registers an alias for a specified command word.
     *
     * @param commandWord the built-in command word that corresponds to the alias.
     * @param alias the alias to be registered.
     * @throws IllegalArgumentException if the {@code commandWord} is unsupported,
     *                                  the alias format is invalid,
     *                                  the alias conflicts with a built-in command or
     *                                  the alias conflicts with another alias previously defined.
     */
    public void register(String commandWord, String alias) throws IllegalArgumentException {
        requireAllNonNull(commandWord, alias);

        checkArgument(builtInCommandWords.contains(commandWord),
                "Unsupported command word " + commandWord);

        checkArgument(alias.matches("\\S+"),
                "Alias must be non-empty and contain no whitespace.");

        checkArgument(!builtInCommandWords.contains(alias),
                "Alias conflicts with a built-in command word: " + alias);

        checkArgument(!aliases.containsKey(alias),
                "Alias already exists: " + alias);

        aliases.put(alias, commandWord);
    }

    /**
     * Returns the corresponding command word if the input is a registered
     * alias. Otherwise, returns the supplied word.
     * @param word the word to be resolved.
     * @return the corresponding command word or the supplied word if it
     *         is not a registered alias.
     */
    public String resolve(String word) {
        requireNonNull(word);
        return aliases.getOrDefault(word, word);
    }
}
