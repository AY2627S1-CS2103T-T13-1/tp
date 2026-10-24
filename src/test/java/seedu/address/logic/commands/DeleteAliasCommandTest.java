package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class DeleteAliasCommandTest {
    private final Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);

    @Test
    public void execute_registeredAlias_preservesOtherMappingsAndReturnsSuccess() throws CommandException {
        model.registerAlias("find", "f");
        model.registerAlias("list", "l");

        CommandResult result = new DeleteAliasCommand("f").execute(model);

        assertEquals(new CommandResult(String.format(DeleteAliasCommand.MESSAGE_SUCCESS, "f")), result);
        assertEquals(Map.of("l", "list"), model.getAliases());
    }

    @Test
    public void execute_unregisteredAlias_throwsCommandExceptionAndPreservesMappings() {
        model.registerAlias("find", "f");

        assertThrows(CommandException.class, "Alias unknown was not previously registered!", () ->
                new DeleteAliasCommand("unknown").execute(model));
        assertEquals(Map.of("f", "find"), model.getAliases());
    }

    @Test
    public void equals() {
        DeleteAliasCommand command = new DeleteAliasCommand("f");

        // same object
        assertTrue(command.equals(command));

        // same field values and hash codes
        DeleteAliasCommand otherCommand = new DeleteAliasCommand("f");
        assertTrue(command.equals(otherCommand));
        assertEquals(command.hashCode(), otherCommand.hashCode());

        // different type and null
        assertFalse(command.equals(1));
        assertFalse(command.equals(null));

        // different alias, including different case
        assertFalse(command.equals(new DeleteAliasCommand("search")));
        assertFalse(command.equals(new DeleteAliasCommand("F")));
    }
}
