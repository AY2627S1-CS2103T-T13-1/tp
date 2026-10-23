package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;

public class ListAliasCommandTest {
    private final Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
    private final ListAliasCommand command = new ListAliasCommand();

    @Test
    public void execute_noAliases_returnsEmptyMessage() throws CommandException {
        assertEquals(new CommandResult(ListAliasCommand.MESSAGE_FAILURE_EMPTY), command.execute(model));
    }

    @Test
    public void execute_singleAlias_returnsCommandAndAlias() throws CommandException {
        model.registerAlias("find", "f");
        String expectedMessage = ListAliasCommand.MESSAGE_SUCCESS_PREFIX + "\nfind: f";
        assertEquals(new CommandResult(expectedMessage), command.execute(model));
    }

    @Test
    public void execute_multipleAliases_groupsMappingsAndPreservesModelState() throws CommandException {
        // Non-boundary: several aliases across commands, including a shared command and distinct cases.
        model.registerAlias("find", "f");
        model.registerAlias("find", "F");
        model.registerAlias("list", "ls");

        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.updateFilteredPersonList(ALICE::equals);
        Map<String, String> expectedAliases = model.getAliases();
        List<Person> expectedPersons = new ArrayList<>(model.getAddressBook().getPersonList());
        List<Person> expectedFilteredPersons = new ArrayList<>(model.getFilteredPersonList());

        List<String> lines = command.execute(model).getFeedbackToUser().lines().toList();

        // Neither command order nor alias order is specified.
        assertEquals(3, lines.size());
        assertEquals(ListAliasCommand.MESSAGE_SUCCESS_PREFIX, lines.get(0));
        List<String> mappings = lines.subList(1, lines.size());
        assertTrue(mappings.contains("list: ls"));
        assertTrue(mappings.contains("find: f, F") || mappings.contains("find: F, f"));
        assertEquals(expectedAliases, model.getAliases());
        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
        assertEquals(expectedFilteredPersons, model.getFilteredPersonList());
    }
}
