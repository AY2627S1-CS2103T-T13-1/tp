package seedu.address.model.alias;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Stores the aliases for the current application session.
 */
public class AliasRegistry {

    public static final String MESSAGE_REGEX_MISMATCH = "Alias must contain only upper or "
            + "lower-case ASCII letters (a-z or A-Z).";

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
    public void register(String commandWord, String alias) {
        requireAllNonNull(commandWord, alias);

        checkArgument(builtInCommandWords.contains(commandWord),
                "Unsupported command word " + commandWord);

        checkArgument(isValidAlias(alias), MESSAGE_REGEX_MISMATCH);

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

    /**
     * Validates if the supplied alias contains only ASCII upper and lower-case
     * characters.
     *
     * @param alias The alias to validate.
     * @return {@code true} if the alias contains one or more upper and lower-case
     *         characters, or {@code false} otherwise.
     */
    public static boolean isValidAlias(String alias) {
        requireNonNull(alias);
        return alias.matches("[a-zA-Z]+");
    }

    /**
     * Returns an immutable snapshot mapping registered aliases to
     * their corresponding command words.
     *
     * @return a map whose keys are aliases and values are command words.
     */
    public Map<String, String> getAliases() {
        return Map.copyOf(aliases);
    }

    /**
     * Removes or de-registers a registered alias.
     *
     * @param alias the alias to remove.
     * @throws IllegalArgumentException if the alias has an invalid format or is not registered.
     */
    public void deleteAlias(String alias) {
        requireNonNull(alias);

        checkArgument(isValidAlias(alias), MESSAGE_REGEX_MISMATCH);
        checkArgument(aliases.containsKey(alias), "Alias " + alias
                + " was not previously registered!");

        aliases.remove(alias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AliasRegistry otherRegistry)) {
            return false;
        }

        return aliases.equals(otherRegistry.aliases)
                && builtInCommandWords.equals(otherRegistry.builtInCommandWords);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(aliases, builtInCommandWords);
    }
}
