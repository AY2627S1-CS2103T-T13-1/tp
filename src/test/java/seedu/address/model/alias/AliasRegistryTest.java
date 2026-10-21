package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AliasRegistryTest {
    private final AliasRegistry registry = new AliasRegistry(Set.of("find", "list", "alias"));

    @Test
    public void constructor_nullCommandWords_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AliasRegistry(null));
    }

    @Test
    public void constructor_commandWordsModified_usesOriginalCommandWords() {
        Set<String> commandWords = new HashSet<>(Set.of("find", "list"));
        AliasRegistry aliasRegistry = new AliasRegistry(commandWords);

        commandWords.remove("find");
        commandWords.add("delete");

        aliasRegistry.register("find", "f");
        assertEquals("find", aliasRegistry.resolve("f"));
        assertThrows(IllegalArgumentException.class, () ->
                aliasRegistry.register("delete", "d"));
        assertEquals("d", aliasRegistry.resolve("d"));
    }

    @Test
    public void register_validAliases_preservesCaseAndResolvesCommands() {
        // Boundary: a one-character alias registered in an empty registry.
        registry.register("find", "f");
        assertEquals("find", registry.resolve("f"));
        assertEquals("F", registry.resolve("F"));

        // Non-boundary: a longer alias registered in a non-empty registry.
        registry.register("find", "search");
        assertEquals("find", registry.resolve("search"));

        // Case-sensitive aliases can be registered independently.
        registry.register("list", "F");
        assertEquals("list", registry.resolve("F"));
        assertEquals("find", registry.resolve("f"));
    }

    @Test
    public void register_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                registry.register(null, "f"));
        assertThrows(NullPointerException.class, () ->
                registry.register("find", null));
    }

    @Test
    public void register_unsupportedCommandWords_preservesExistingAliases() {
        registry.register("find", "f");

        // Unsupported target partitions: unknown word and an existing alias.
        for (String commandWord : new String[] {"unknown", "f"}) {
            assertThrows(IllegalArgumentException.class, () ->
                    registry.register(commandWord, "newAlias"));
            assertEquals("newAlias", registry.resolve("newAlias"));
            assertEquals("find", registry.resolve("f"));
        }
    }

    @Test
    public void register_invalidAliasFormats_preservesExistingAliases() {
        registry.register("find", "f");

        // Boundary: empty alias. Non-boundary: an alias containing whitespace.
        for (String alias : new String[] {"", "two words"}) {
            assertThrows(IllegalArgumentException.class, () ->
                    registry.register("list", alias));
            assertEquals(alias, registry.resolve(alias));
            assertEquals("find", registry.resolve("f"));
        }
    }

    @Test
    public void register_conflictingAliases_preservesOriginalMappings() {
        registry.register("find", "f");

        // Conflict partitions: a built-in command word and an existing alias.
        assertThrows(IllegalArgumentException.class, () ->
                registry.register("find", "list"));
        assertEquals("list", registry.resolve("list"));

        assertThrows(IllegalArgumentException.class, () ->
                registry.register("find", "f"));
        assertThrows(IllegalArgumentException.class, () ->
                registry.register("list", "f"));
        assertEquals("find", registry.resolve("f"));
    }

    @Test
    public void resolve_unregisteredWords_returnsSuppliedWords() {
        assertEquals("unknown", registry.resolve("unknown"));
        assertEquals("find", registry.resolve("find"));
    }

    @Test
    public void resolve_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.resolve(null));
    }

    @Test
    public void register_separateRegistries_doNotShareAliases() {
        registry.register("find", "f");
        AliasRegistry otherRegistry = new AliasRegistry(Set.of("find", "list", "alias"));

        assertEquals("f", otherRegistry.resolve("f"));
        otherRegistry.register("list", "f");
        assertEquals("list", otherRegistry.resolve("f"));
        assertEquals("find", registry.resolve("f"));
    }
}
