package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AliasRegistryTest {
    private final AliasRegistry registry = new AliasRegistry(Set.of("find", "list", "alias"));

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
    public void register_separateRegistries_doNotShareAliases() {
        registry.register("find", "f");
        AliasRegistry otherRegistry = new AliasRegistry(Set.of("find", "list", "alias"));

        assertEquals("f", otherRegistry.resolve("f"));
        otherRegistry.register("list", "f");
        assertEquals("list", otherRegistry.resolve("f"));
        assertEquals("find", registry.resolve("f"));
    }

    @Test
    public void getAliases_emptyAndPopulatedRegistry_returnsAllMappings() {
        // Boundary: an empty registry.
        assertEquals(Map.of(), registry.getAliases());

        // Boundary: the first alias registered.
        registry.register("find", "f");
        assertEquals(Map.of("f", "find"), registry.getAliases());

        // Non-boundary: multiple aliases, including distinct cases and a shared command.
        registry.register("list", "F");
        registry.register("find", "search");
        assertEquals(Map.of("f", "find", "F", "list", "search", "find"), registry.getAliases());
    }

    @Test
    public void getAliases_modifySnapshot_throwsUnsupportedOperationException() {
        registry.register("find", "f");
        Map<String, String> snapshot = registry.getAliases();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.put("l", "list"));
        assertEquals(Map.of("f", "find"), snapshot);
        assertEquals(Map.of("f", "find"), registry.getAliases());
    }

    @Test
    public void getAliases_registryUpdated_preservesEarlierSnapshots() {
        Map<String, String> emptySnapshot = registry.getAliases();
        registry.register("find", "f");
        Map<String, String> populatedSnapshot = registry.getAliases();

        registry.register("list", "l");

        assertEquals(Map.of(), emptySnapshot);
        assertEquals(Map.of("f", "find"), populatedSnapshot);
        assertEquals(Map.of("f", "find", "l", "list"), registry.getAliases());
    }

    @Test
    public void deleteAlias_invalidAliasFormats_preservesExistingAliases() {
        registry.register("find", "f");

        // Boundary: empty alias. Non-boundary: alias containing whitespace.
        for (String alias : new String[] {"", "two words"}) {
            assertThrows(IllegalArgumentException.class, AliasRegistry.MESSAGE_REGEX_MISMATCH, () ->
                    registry.deleteAlias(alias));
            assertEquals(Map.of("f", "find"), registry.getAliases());
        }
    }

    @Test
    public void deleteAlias_unregisteredAliases_preservesExistingAliases() {
        // Boundary: empty registry.
        assertThrows(IllegalArgumentException.class, () -> registry.deleteAlias("unknown"));
        assertEquals(Map.of(), registry.getAliases());

        registry.register("find", "f");

        // Absent alias partitions: unknown word, built-in command and different case.
        for (String alias : new String[] {"unknown", "find", "F"}) {
            assertThrows(IllegalArgumentException.class, "Alias " + alias + " was not previously registered!", () ->
                    registry.deleteAlias(alias));
            assertEquals(Map.of("f", "find"), registry.getAliases());
        }
    }

    @Test
    public void deleteAlias_onlyRegisteredAlias_emptiesRegistry() {
        // Boundaries: a one-character alias and a registry with one entry.
        registry.register("find", "f");

        registry.deleteAlias("f");

        assertEquals(Map.of(), registry.getAliases());
        assertEquals("f", registry.resolve("f"));
    }

    @Test
    public void deleteAlias_multipleRegisteredAliases_preservesOtherMappings() {
        registry.register("find", "search");
        registry.register("find", "f");
        registry.register("list", "F");

        // Non-boundary: a longer alias in a registry with multiple entries.
        registry.deleteAlias("search");

        assertEquals(Map.of("f", "find", "F", "list"), registry.getAliases());
        assertEquals("search", registry.resolve("search"));
        assertEquals("find", registry.resolve("f"));
        assertEquals("list", registry.resolve("F"));

        // Deletion is case-sensitive even when both cases are registered.
        registry.deleteAlias("f");

        assertEquals(Map.of("F", "list"), registry.getAliases());
        assertEquals("f", registry.resolve("f"));
        assertEquals("list", registry.resolve("F"));
    }

    @Test
    public void deleteAlias_previouslyDeletedAlias_throwsIllegalArgumentExceptionAndAllowsReuse() {
        registry.register("find", "f");
        registry.register("list", "l");
        registry.deleteAlias("f");

        assertThrows(IllegalArgumentException.class, () -> registry.deleteAlias("f"));
        assertEquals(Map.of("l", "list"), registry.getAliases());

        registry.register("list", "f");
        assertEquals(Map.of("f", "list", "l", "list"), registry.getAliases());
        assertEquals("list", registry.resolve("f"));
    }

    @Test
    public void equals() {
        AliasRegistry otherRegistry = new AliasRegistry(Set.of("find", "list", "alias"));

        assertTrue(registry.equals(registry));
        assertTrue(registry.equals(otherRegistry));
        assertFalse(registry.equals(null));
        assertFalse(registry.equals("registry"));

        // Different alias mappings -> returns false.
        registry.register("find", "f");
        assertFalse(registry.equals(otherRegistry));

        // Same alias mappings -> returns true.
        otherRegistry.register("find", "f");
        assertTrue(registry.equals(otherRegistry));

        // Same mappings but different supported commands -> returns false.
        AliasRegistry differentCommandsRegistry = new AliasRegistry(Set.of("find", "list"));
        differentCommandsRegistry.register("find", "f");
        assertFalse(registry.equals(differentCommandsRegistry));
    }

    @Test
    public void hashCode_equalRegistries_returnsSameHashCode() {
        AliasRegistry otherRegistry = new AliasRegistry(Set.of("find", "list", "alias"));
        registry.register("find", "f");
        otherRegistry.register("find", "f");

        assertEquals(registry.hashCode(), otherRegistry.hashCode());
    }
}
