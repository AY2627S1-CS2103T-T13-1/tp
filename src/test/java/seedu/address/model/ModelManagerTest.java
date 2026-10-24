package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager(BUILT_IN_COMMAND_WORDS);

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs, BUILT_IN_COMMAND_WORDS);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getAliases_aliasesRegisteredThroughModel_returnsCurrentMappings() {
        // Boundary: the model has no registered aliases.
        assertEquals(Map.of(), modelManager.getAliases());

        // Non-empty state: registrations are exposed through the model.
        modelManager.registerAlias("find", "f");
        modelManager.registerAlias("list", "l");
        assertEquals(Map.of("f", "find", "l", "list"), modelManager.getAliases());
    }

    @Test
    public void deleteAlias_registeredAlias_removesOnlySpecifiedAlias() {
        modelManager.registerAlias("find", "f");
        modelManager.registerAlias("list", "l");
        modelManager.deleteAlias("f");
        assertEquals(Map.of("l", "list"), modelManager.getAliases());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs, BUILT_IN_COMMAND_WORDS);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs, BUILT_IN_COMMAND_WORDS);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs, BUILT_IN_COMMAND_WORDS)));

        // different filteredList -> returns false
        modelManager.updateFilteredPersonList(new PersonMatchesCriteriaPredicate(Optional.of(ALICE.getName().fullName),
                Optional.empty(), Optional.empty(), Optional.empty(), Set.of()));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs, BUILT_IN_COMMAND_WORDS)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs, BUILT_IN_COMMAND_WORDS)));

        // different alias mappings -> returns false
        modelManager.registerAlias("find", "f");
        assertFalse(modelManager.equals(modelManagerCopy));

        // same alias mappings in separate registries -> returns true
        modelManagerCopy.registerAlias("find", "f");
        assertTrue(modelManager.equals(modelManagerCopy));
    }
}
