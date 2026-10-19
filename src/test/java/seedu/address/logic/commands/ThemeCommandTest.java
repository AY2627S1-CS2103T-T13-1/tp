package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Theme;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class ThemeCommandTest {
    private Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
    private Model expectedModel = new ModelManager(BUILT_IN_COMMAND_WORDS);

    @Test
    public void constructor_nullTheme_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ThemeCommand(null));
    }

    @Test
    public void execute_dark_success() {
        CommandResult expectedCommandResult = new CommandResult(
                String.format(ThemeCommand.MESSAGE_SUCCESS, "dark"), false, Theme.DARK);
        assertCommandSuccess(new ThemeCommand(Theme.DARK), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_light_success() {
        CommandResult expectedCommandResult = new CommandResult(
                String.format(ThemeCommand.MESSAGE_SUCCESS, "light"), false, Theme.LIGHT);
        assertCommandSuccess(new ThemeCommand(Theme.LIGHT), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void equals() {
        ThemeCommand darkCommand = new ThemeCommand(Theme.DARK);
        ThemeCommand lightCommand = new ThemeCommand(Theme.LIGHT);

        // same object -> returns true
        assertTrue(darkCommand.equals(darkCommand));

        // same values -> returns true
        assertTrue(darkCommand.equals(new ThemeCommand(Theme.DARK)));

        // different types -> returns false
        assertFalse(darkCommand.equals(1));

        // null -> returns false
        assertFalse(darkCommand.equals(null));

        // different theme -> returns false
        assertFalse(darkCommand.equals(lightCommand));
    }

    @Test
    public void toStringMethod() {
        ThemeCommand themeCommand = new ThemeCommand(Theme.LIGHT);
        String expected = ThemeCommand.class.getCanonicalName() + "{theme=" + Theme.LIGHT + "}";
        assertEquals(expected, themeCommand.toString());
    }
}
