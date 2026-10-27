package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_EMPTY_LIST;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonAliasStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        model.addPerson(AMY);

        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, DeleteCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void execute_deleteFromEmptyList_throwsCommandException() {
        String deleteCommand = "delete 1";
        assertCommandException(deleteCommand, MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        model.addPerson(AMY);
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandException(listCommand, MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_undoAfterAdd_removesAddedPerson() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(ListCommand.COMMAND_WORD);

        // list does not change the contact list, so undo reverses the add command instead
        assertCommandSuccess(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_SUCCESS,
                new ModelManager(BUILT_IN_COMMAND_WORDS));

        // verify that undo persists the restored contacts to disk
        assertEquals(new AddressBook(),
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                        .readAddressBook().orElseThrow());

        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_undoAfterMultipleCommands_restoresStatesInReverseOrder() throws Exception {
        Person amy = new PersonBuilder(AMY).withTags().build();
        Person bob = new PersonBuilder(BOB).withTags().build();
        Person editedAmy = new PersonBuilder(amy).withAddress(VALID_ADDRESS_BOB).build();
        Person pinnedBob = bob.pinPerson();

        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_BOB + PHONE_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB);
        logic.execute(EditCommand.COMMAND_WORD + " 1" + ADDRESS_DESC_BOB);
        logic.execute(PinCommand.COMMAND_WORD + " 2");
        logic.execute(DeleteCommand.COMMAND_WORD + " 2");
        logic.execute(ListCommand.COMMAND_WORD);
        logic.execute(ClearCommand.COMMAND_WORD);

        // list does not change the contact list, so it is skipped
        assertUndoSuccess(pinnedBob);
        assertUndoSuccess(pinnedBob, editedAmy);
        assertUndoSuccess(editedAmy, bob);
        assertUndoSuccess(amy, bob);
        assertUndoSuccess(amy);
        assertUndoSuccess();
        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_redoAfterUndo_restoresAddedPerson() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(UndoCommand.COMMAND_WORD);

        // find does not change the contact list, so it does not clear the redo history
        logic.execute(FindCommand.COMMAND_WORD + NAME_DESC_AMY);

        ModelManager expectedModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
        expectedModel.addPerson(new PersonBuilder(AMY).withTags().build());
        assertCommandSuccess(RedoCommand.COMMAND_WORD, RedoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandException(RedoCommand.COMMAND_WORD, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_duplicateEmailEdit_preservesContactsStorageAndUndoHistory() throws Exception {
        assertDuplicateEditPreservesSession(EMAIL_DESC_BOB);
    }

    @Test
    public void execute_duplicatePhoneEdit_preservesContactsStorageAndUndoHistory() throws Exception {
        assertDuplicateEditPreservesSession(PHONE_DESC_BOB);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_clearAfterSaveFailure_retriesSavingUnchangedContacts() throws Exception {
        model.addPerson(AMY);
        AddressBook originalContacts = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        addressBookStorage.saveAddressBook(originalContacts);
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        boolean[] storageUnwritable = {true};
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS)) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (storageUnwritable[0]) {
                    throw DUMMY_AD_EXCEPTION;
                }
                super.saveAddressBook(addressBook);
            }
        };
        logic = new LogicManager(model, storage);

        assertThrows(CommandException.class, () -> logic.execute(ClearCommand.COMMAND_WORD));
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(originalContacts, addressBookStorage.readAddressBook().orElseThrow());

        storageUnwritable[0] = false;
        CommandResult result = logic.execute(ClearCommand.COMMAND_WORD);

        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(new AddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteAlias_deletesAliasWithoutSavingContacts() throws Exception {
        model.addPerson(AMY);
        model.registerAlias("find", "f");
        AddressBook originalContacts = new AddressBook(model.getAddressBook());

        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));

        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS)) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                throw new AssertionError("Deleting an alias must not save contacts.");
            }

            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path path) {
                throw new AssertionError("Deleting an alias must not export contacts.");
            }
        };
        logic = new LogicManager(model, storage);

        CommandResult result = logic.execute("delete_alias f");

        assertEquals(
                new CommandResult(String.format(DeleteAliasCommand.MESSAGE_SUCCESS, "f")),
                result);
        assertEquals(Map.of(), model.getAliases());
        assertEquals(originalContacts, model.getAddressBook());
    }

    @Test
    public void execute_exportSuccess_savesExportOnly() throws Exception {
        model.addPerson(AMY);
        List<ReadOnlyAddressBook> exportedAddressBooks = new ArrayList<>();
        List<Path> exportedFilePaths = new ArrayList<>();
        int[] regularSaveCount = {0};

        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("exportAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("exportUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS)) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path path) {
                exportedAddressBooks.add(addressBook);
                exportedFilePaths.add(path);
            }

            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                regularSaveCount[0]++;
            }
        };
        logic = new LogicManager(model, storage);

        CommandResult result = logic.execute("export -f samplefile");

        assertEquals("Contact(s) exported to samplefile.json", result.getFeedbackToUser());
        assertEquals(List.of(model.getAddressBook()), exportedAddressBooks);
        assertEquals(List.of(Path.of("data", "exports", "samplefile.json")), exportedFilePaths);
        assertEquals(0, regularSaveCount[0]);
    }

    @Test
    public void execute_exportStorageThrowsIoException_throwsCommandException() {
        model.addPerson(AMY);
        IOException exception = new IOException("dummy export IO exception");
        setUpLogicWithExportStorageException(exception);

        assertCommandFailure("export -f samplefile", CommandException.class,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, exception.getMessage()));
    }

    @Test
    public void execute_exportStorageThrowsAccessDeniedException_throwsCommandException() {
        model.addPerson(AMY);
        AccessDeniedException exception = new AccessDeniedException("dummy export access denied exception");
        setUpLogicWithExportStorageException(exception);

        assertCommandFailure("export -f samplefile", CommandException.class,
                String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, exception.getMessage()));
    }

    @Test
    public void execute_importSuccess_mergesContactsAndSavesAddressBook() throws Exception {
        AddressBook importedAddressBook = new AddressBookBuilder()
                .withPerson(AMY)
                .withPerson(BOB)
                .build();
        Path importFilePath = temporaryFolder.resolve("contactsToImport.json");
        new JsonAddressBookStorage(importFilePath).saveAddressBook(importedAddressBook);

        CommandResult result = logic.execute("import -f " + importFilePath);

        assertEquals("Imported 2 contact(s). Skipped 0 duplicate(s).", result.getFeedbackToUser());
        assertEquals(importedAddressBook, new AddressBook(model.getAddressBook()));

        Path savedFilePath = temporaryFolder.resolve("addressBook.json");
        ReadOnlyAddressBook savedAddressBook =
                new JsonAddressBookStorage(savedFilePath).readAddressBook().orElseThrow();
        assertEquals(importedAddressBook, new AddressBook(savedAddressBook));
    }

    @Test
    public void execute_importWithDuplicates_skipsDuplicates() throws Exception {
        model.addPerson(AMY);
        Person duplicatePerson = new PersonBuilder(AMY)
                .withName("Different Person")
                .build();
        AddressBook importedAddressBook = new AddressBookBuilder()
                .withPerson(duplicatePerson)
                .withPerson(BOB)
                .build();
        Path importFilePath = temporaryFolder.resolve("contactsWithDuplicates.json");
        new JsonAddressBookStorage(importFilePath).saveAddressBook(importedAddressBook);

        CommandResult result = logic.execute("import -f " + importFilePath);

        assertEquals("Imported 1 contact(s). Skipped 1 duplicate(s).", result.getFeedbackToUser());
        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertEquals(AMY, model.getAddressBook().getPersonList().stream()
                .filter(person -> person.isSamePerson(AMY))
                .findFirst()
                .orElseThrow());
        assertTrue(model.hasPerson(BOB));
    }

    @Test
    public void execute_importThenUndo_restoresPreviousAddressBook() throws Exception {
        AddressBook importedAddressBook = new AddressBookBuilder()
                .withPerson(AMY)
                .build();
        Path importFilePath = temporaryFolder.resolve("contactsToUndo.json");
        new JsonAddressBookStorage(importFilePath).saveAddressBook(importedAddressBook);

        logic.execute("import -f " + importFilePath);
        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(new AddressBook(), new AddressBook(model.getAddressBook()));
    }

    @Test
    public void execute_importMissingFile_throwsCommandExceptionAndLeavesModelUnchanged() {
        Path missingFilePath = temporaryFolder.resolve("missingContacts.json");
        Model expectedModel = new ModelManager(BUILT_IN_COMMAND_WORDS);

        assertCommandFailure(
                "import -f " + missingFilePath,
                CommandException.class,
                String.format(LogicManager.FILE_IMPORT_ERROR_FORMAT,
                        missingFilePath,
                        LogicManager.FILE_NOT_FOUND),
                expectedModel);
    }

    @Test
    public void execute_importSaveFailsThenLaterCommandRetriesSave() throws Exception {
        AddressBook importedAddressBook = new AddressBookBuilder()
                .withPerson(AMY)
                .build();
        Path importFilePath = temporaryFolder.resolve("contactsToRetry.json");
        new JsonAddressBookStorage(importFilePath).saveAddressBook(importedAddressBook);

        Path addressBookFilePath = temporaryFolder.resolve("addressBookToRetry.json");
        int[] regularSaveAttempts = {0};
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("retryUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS)) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                regularSaveAttempts[0]++;
                if (regularSaveAttempts[0] == 1) {
                    throw DUMMY_IO_EXCEPTION;
                }
                super.saveAddressBook(addressBook);
            }
        };
        logic = new LogicManager(model, storage);

        Model expectedModel = new ModelManager(
                importedAddressBook,
                new UserPrefs(),
                BUILT_IN_COMMAND_WORDS);
        assertCommandFailure(
                "import -f " + importFilePath,
                CommandException.class,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()),
                expectedModel);
        assertEquals(1, regularSaveAttempts[0]);

        CommandResult result = logic.execute(ListCommand.COMMAND_WORD);

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(2, regularSaveAttempts[0]);
        assertEquals(importedAddressBook,
                new AddressBook(addressBookStorage.readAddressBook().orElseThrow()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Verifies that a rejected identity-field edit leaves contacts and storage unchanged,
     * and that subsequent list and undo commands still work without an extra history entry.
     */
    private void assertDuplicateEditPreservesSession(String duplicateField) throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_BOB + PHONE_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB);
        AddressBook originalContacts = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(originalContacts, addressBookStorage.readAddressBook().orElseThrow());

        assertCommandException(EditCommand.COMMAND_WORD + " 1" + duplicateField,
                EditCommand.MESSAGE_DUPLICATE_PERSON);

        assertEquals(originalContacts, model.getAddressBook());
        assertEquals(originalContacts, addressBookStorage.readAddressBook().orElseThrow());
        CommandResult listResult = logic.execute(ListCommand.COMMAND_WORD);
        assertEquals(ListCommand.MESSAGE_SUCCESS, listResult.getFeedbackToUser());
        assertEquals(originalContacts, model.getAddressBook());

        Person amy = new PersonBuilder(AMY).withTags().build();
        assertUndoSuccess(amy);
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertUndoSuccess();
        assertEquals(new AddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    /**
     * Executes the undo command and confirms that the address book contains exactly {@code expectedPersons},
     * in the given order, with all of them shown.
     */
    private void assertUndoSuccess(Person... expectedPersons) throws CommandException, ParseException {
        AddressBookBuilder addressBookBuilder = new AddressBookBuilder();
        for (Person person : expectedPersons) {
            addressBookBuilder.withPerson(person);
        }
        Model expectedModel =
                new ModelManager(addressBookBuilder.build(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        assertCommandSuccess(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS));

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    /**
     * Replaces the logic's storage with one that fails when saving an exported address book.
     */
    private void setUpLogicWithExportStorageException(IOException exception) {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("exportExceptionAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("exportExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                new JsonAliasStorage(temporaryFolder.resolve("aliases.json"), BUILT_IN_COMMAND_WORDS)) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path path) throws IOException {
                throw exception;
            }

            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                // The export save fails before the regular address book save is reached.
            }
        };
        logic = new LogicManager(model, storage);
    }
}
