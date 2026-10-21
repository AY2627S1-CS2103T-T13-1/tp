package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.alias.AliasRegistry;

public class AliasCommandTest {
    private final Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AliasCommand(null, "f"));
        assertThrows(NullPointerException.class, () -> new AliasCommand("find", null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new AliasCommand("find", "f").execute(null));
    }

    @Test
    public void execute_validAliases_registersOnlyOnExecutionAndReturnsSuccess() throws CommandException {
        // Boundary: a one-character alias in an empty registry.
        AliasCommand command = new AliasCommand("find", "f");
        assertEquals("f", model.getAliasRegistry().resolve("f"));

        CommandResult result = command.execute(model);

        assertEquals("find", model.getAliasRegistry().resolve("f"));
        assertEquals(new CommandResult(String.format(AliasCommand.MESSAGE_SUCCESS, "f", "find")), result);

        // Non-boundary: a longer alias added without replacing the existing alias.
        CommandResult secondResult = new AliasCommand("find", "search").execute(model);

        assertEquals("find", model.getAliasRegistry().resolve("search"));
        assertEquals("find", model.getAliasRegistry().resolve("f"));
        assertEquals(new CommandResult(String.format(AliasCommand.MESSAGE_SUCCESS, "search", "find")), secondResult);
    }

    @Test
    public void execute_caseDistinctAliases_preservesIndependentMappings() throws CommandException {
        new AliasCommand("find", "f").execute(model);
        assertEquals("F", model.getAliasRegistry().resolve("F"));

        // Regression: registering F must neither conflict with f nor overwrite its mapping.
        new AliasCommand("list", "F").execute(model);

        assertEquals("find", model.getAliasRegistry().resolve("f"));
        assertEquals("list", model.getAliasRegistry().resolve("F"));
    }

    @Test
    public void execute_unsupportedCommandWords_throwsCommandException() {
        model.getAliasRegistry().register("find", "f");

        // Unsupported target partitions: an unknown command and an existing alias.
        for (String commandWord : new String[] {"unknown", "f"}) {
            assertRegistrationFailure(commandWord, "newAlias",
                    "Unsupported command word " + commandWord);
        }
    }

    @Test
    public void execute_invalidAliasFormats_throwsCommandException() {
        model.getAliasRegistry().register("find", "f");

        // Boundary: an empty alias. Non-boundary: an alias containing whitespace.
        for (String alias : new String[] {"", "two words"}) {
            assertRegistrationFailure("list", alias,
                    AliasRegistry.MESSAGE_REGEX_MISMATCH);
        }
    }

    @Test
    public void execute_builtinCommandConflict_throwsCommandException() {
        model.getAliasRegistry().register("find", "f");

        assertRegistrationFailure("find", "list",
                "Alias conflicts with a built-in command word: list");
    }

    @Test
    public void execute_duplicateAlias_throwsCommandException() {
        model.getAliasRegistry().register("find", "f");

        // Reusing an alias is rejected for both the same command and a different command.
        for (String commandWord : new String[] {"find", "list"}) {
            assertRegistrationFailure(commandWord, "f", "Alias already exists: f");
        }
    }

    /**
     * Asserts that execution reports the validation error and preserves the existing mappings.
     * Each caller seeds the registry with the alias f for find.
     */
    private void assertRegistrationFailure(String commandWord, String alias, String expectedMessage) {
        AliasCommand command = new AliasCommand(commandWord, alias);
        String originalResolution = model.getAliasRegistry().resolve(alias);

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));

        assertEquals(originalResolution, model.getAliasRegistry().resolve(alias));
        assertEquals("find", model.getAliasRegistry().resolve("f"));
    }

    @Test
    public void equals() {
        AliasCommand command = new AliasCommand("find", "f");

        // same object
        assertTrue(command.equals(command));

        // same field values
        assertTrue(command.equals(new AliasCommand("find", "f")));

        // different type and null
        assertFalse(command.equals(1));
        assertFalse(command.equals(null));

        // each field differs independently
        assertFalse(command.equals(new AliasCommand("list", "f")));
        assertFalse(command.equals(new AliasCommand("find", "search")));

        // alias comparison is case-sensitive
        assertFalse(command.equals(new AliasCommand("find", "F")));
    }
}
