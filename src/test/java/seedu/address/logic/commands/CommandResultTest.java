package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Theme;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true)));

        // different theme value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, Theme.LIGHT)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true).hashCode());

        // different theme value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, Theme.LIGHT).hashCode());
    }

    @Test
    public void getTheme() {
        assertTrue(new CommandResult("feedback").getTheme().isEmpty());
        assertEquals(Theme.DARK, new CommandResult("feedback", false, Theme.DARK).getTheme().get());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser()
                + ", exit=" + commandResult.isExit() + ", theme=null}";
        assertEquals(expected, commandResult.toString());
    }
}
