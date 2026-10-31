---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# coNnectUS Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This brownfield project was based on AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org/).

In addition, we also:
* adapted [this solution](https://stackoverflow.com/a/5226244) to open the User Guide URL from the `UserGuideAccessWindow` in user's default browser.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* keeps a history of address book states in a `VersionedAddressBook`, so that changes can be undone and redone (see [Undo/redo feature](#undo-redo-feature)).
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-T13-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Dark and light themes

The user can switch between an OLED dark theme and a light theme with `theme dark` / `theme light`, or with the theme button in the menu bar.

<puml src="diagrams/ThemeSequenceDiagram.puml" alt="Sequence of the `theme light` command" />

The colors are split across two kinds of stylesheets in `src/main/resources/view`. `Base.css`, `Extensions.css` and `UserGuideAccessWindow.css` hold all the style rules, but use color placeholders (e.g. `theme-bg`, `theme-text`) instead of actual colors. `DarkTheme.css` and `LightTheme.css` only define the color of each placeholder. Switching themes swaps this one palette stylesheet.

The theme feature,

* uses `ThemeCommand`, which returns a `CommandResult` carrying the requested `Theme` instead of changing the `Model`, the same way `exit` asks the UI to close the app.
* uses `ThemeStylesheet` to swap the palette stylesheet of both `MainWindow` and `UserGuideAccessWindow`, so the two windows always match.
* uses the same `MainWindow#applyTheme(...)` method for both the command and the button.
* keeps the current theme and shows an error message if the stylesheet of the new theme cannot be found.
* saves the theme in `GuiSettings`, which is written to `preferences.json` on exit. If no theme is saved, `Theme.DARK` is used.

### Sorting contacts
<puml src="diagrams/SortSequenceDiagram.puml" alt="Sequence of sort command"/>

Users are able to sort their contacts such as by name and by their phone numbers.

The sort feature makes use of the `PersonComparatorFactory` class to create comparators for the various sorts.
- `PersonComparatorFactory` provides `createComparator(SortCriteria, SortOrder)` to create the various comparators required to achieve the sorts.
- `PersonComparatorFactory` also has a private `SortComparator` class with the intention to override `equals()` to provide a more accurate equality check.
  - Ensuring `equals()` is accurate is needed to prevent `undo/redo` loops associated with `sort`.
- `PersonComparatorFactory` also adopts the singleton design pattern for the default comparator (for pinned persons sorting) to ensure `equals()` is correct for it.
- To ensure that user applied sorts are maintained, `ModelManager` stores the most recent comparator used as `activeComparator`
  - `ModelManager` will call `sortAddressBook` internally after any change to the contacts such as adding or removing contacts


### Pinning a contact

Users are able to pin contacts, placing them above other unpinned contacts.

<puml src="diagrams/PinSequenceDiagram.puml" alt="Sequence of pin command"/>

The pin feature makes use of sorting to ensure pinned contacts are displayed on top of unpinned contacts.
- The default sorting method from `PersonComparatorFactory` sorts persons by their pinned status.
- This sorts the list according to pinned status first, followed by the sorting method previously set by the user (such as by Name).

As for unpinning a contact, the only difference is using `unpinPerson()` instead of `pinPerson()`.

### Registering an alias for a command

Users are able to register aliases for commands, enabling a more customized experience when using coNnectUS.

<puml src="diagrams/AliasSequenceDiagram.puml" alt="Sequence for alias command"/>

The above sequence diagram above illustrates the successful execution of
`alias find f`.

The `alias` feature:
* maintains alias-to-command-word mappings in `AliasRegistry`.
* delegates registration through `Model#registerAlias()` to `AliasRegistry#register()`, which validates the registration and stores the alias-to-command-word mapping.
* requires `alias` to consist of one or more lowercase or uppercase ASCII letters (i.e. `a-z` or `A-Z`), with **no whitespace**.
* treats aliases as **case-sensitive**,  allowing for names such as `f` and `F` to be registered separately.
* supports registration for built-in command words only, using an exact, case-sensitive match. Existing aliases cannot be used for the target command word.
* rejects alias names that conflict with either of the following:
    * a built-in command word (using a _case-insensitive_ comparison).
    * an already registered alias (using a _case-sensitive_ comparison).
    <br>
* resolves an alias to its command word before parsing the target command's arguments. Argument values remain unchanged.
* saves the updated alias mappings through `LogicManager` and the storage component, so that successfully saved registrations persist across sessions, through `data/aliases.json`.

<box type="warning" header="**Important Note**" seamless>

When adding a new built-in command, add its `COMMAND_WORD` to `CommandCatalog.BUILT_IN_COMMAND_WORDS` so that aliases can be registered to it.
</box>

### Deleting a particular alias

Users are able to remove previously registered alias entries that they do not require.

<puml src="diagrams/DeleteAliasSequenceDiagram.puml" alt="Sequence for delete_alias command"/>

The above sequence diagram above illustrates the successful execution of
`delete_alias s`, assuming that the user has previously registered `s` as an alias for `sort`.

The `delete_alias` feature:
* uses `DeleteAliasCommandParser` to validate the alias is supplied before creating a `DeleteAliasCommand`.
* delegates deletion through `Model#deleteAlias()` to `AliasRegistry#deleteAlias()`and requires the alias to exist in the `AliasRegistry`, before deleting it.
* treats alias names as **case-sensitive**, so deleting `f` does not remove a separately registered alias named `F`.
* rejects an unregistered alias with an `IllegalArgumentException`, which `DeleteAliasCommand` converts into a `CommandException` for display to the user.
* saves the updated alias mappings through `LogicManager` and the storage component, so that successfully saved deletions persist across sessions.


### Tags for contacts

#### Implementation

<puml src="diagrams/TagClassDiagram.puml" alt="Class diagram for Tag"/>

* The different tag categories are implemented as subclasses of an abstract `Tag` class.
* When adding a new tag category, the following changes are recommended:
  * Create a new subclass of `Tag`.
  * Modify `src/main/resources/Base.css`, adding a new CSS selector for a background color for the new tag with: `#tags .label.tags-<NEW_TAG_CATEGORY>` and `#tagLegend .label.tag-<NEW_TAG_CATEGORY>`.
  * Then, modify `MainWindow.java`, adding a new `TagCategoryLegendLabel` to `tagLegend` to represent the new tag category.
* The recommended changes ensure that the new tag category is assigned a background color and displayed in the legend at the bottom of the Ui.

### Undo/redo feature

#### Implementation

The undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves a copy of the current address book state in its history, if it differs from the current saved state.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commit()`, `Model#undo()` and `Model#redo()` respectively, together with `Model#canUndo()` and `Model#canRedo()`. The method names do not mention the address book, so that other data (e.g. aliases) can be added to the saved state in the future.

`LogicManager` calls `Model#commit()` after every command that executes successfully. Individual commands do not need to commit, and a command that does not change the contact list (e.g. `list`, `find`, `theme` or `alias`) does not save a new state.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. After the command executes, `LogicManager` calls `Model#commit()`. Since the address book has changed, its new state is saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add -n David …​` to add a new person. `LogicManager` calls `Model#commit()` again, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, `LogicManager` will not call `Model#commit()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undo()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state. The contacts are then sorted again so that pinned contacts stay on top, and all contacts are shown.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndo()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

The `Model#commit()` call made after the `undo` command does not save a new state, since the address book is the same as the restored state.

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redo()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedo()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. `LogicManager` still calls `Model#commit()`, but since `list` does not change the address book, no state is saved. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, after which `LogicManager` calls `Model#commit()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add -n David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

**Aspect: Where the address book state is saved:**

* **Alternative 1 (current choice):** `LogicManager` calls `Model#commit()` after every command, and a state is only saved if the address book has changed.
  * Pros: New commands that change the contact list can be undone without extra code.
  * Cons: The address book is compared with the current saved state after every command.

* **Alternative 2:** Each command that changes the contact list calls `Model#commit()` itself.
  * Pros: No comparison is needed after each command.
  * Cons: A developer may forget to call `Model#commit()` in a new command, which then cannot be undone.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* student from National University of Singapore (NUS).
* prefers desktop apps over other types of applications.
* can type at an average typing speed (45 WPM).
* have difficulty keeping track of friends met in NUS.

**Value proposition**: Our application enables one to maintain a list of their friends/acquaintances met from different parts of school -- friends from common CCAs, classes, different years, TAs.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *` | Regular user | Create a contact | I can remember my friend's contact information |
| `* * *` | Regular user | Delete a contact | I can remove unused contacts |
| `* * *` | Regular user | Tag a contact | I can quickly figure out how this person is associated with me. |
| `* * *` | Regular user | See all details of my contact | I can differentiate between friends with the same name |
| `* *` | Beginner user | View a user guide/tutorial | I can get started using coNnectUS quickly |
| `* *` | Beginner user | See friendly error messages | I can understand what I did wrong and correct them. |
| `* *` | Forgetful user | Search for my contacts using any of their contexts | I can find my friend without remembering their name |
| `* *` | Regular User | Update certain fields in contacts | I can reflect changes in contact information or correct mistakes |
| `* *` | Regular user | Undo or redo my most recent changes | I can quickly recover from a mistake, such as deleting the wrong contact |
| `* *` | Regular user | Group my contacts | I can find easily find friends with the same context |
| `* *` | Regular user | Have added contacts automatically be saved | I don't have to worry about contacts not being saved |
| `*` | Beginner user | Have sample contact information | I can explore features without having to add contacts |
| `*` | Forgetful user | Have a guided command process | I need not remember all the syntax for the commands |
| `*` | Lazy user | Search for my contacts with just parts of their name | I can lazily search for my friend's contact information |
| `*` | Regular user | Toggle between dark/light mode | I can choose the UI style I like best |
| `*` | Regular user | Export only selected contacts | I can share mutual contacts with others. |
| `*` | Regular user | Import a contact | I don't have to enter all their details manually. |
| `*` | Regular User | Contextualize my contacts (free text field) | I can recall where/how I met my friends |
| `*` | Regular User | Sort my contacts | I can find my contacts more easily |
| `*` | Regular User | Have favourite / most-frequented contacts | I can easily navigate to frequently-contacted contacts |
| `*` | Regular User | Store and be reminded of regular meeting times with contacts | I don't forget to show up for them |
| `*` | Expert user | Set aliases for the commands | I can customise the available command words to my liking |
| `*` | Expert user | Delete all contacts with a certain tag | I can remove multiple contacts that are not needed easily. |
| `*` | Expert user | Change the storage location for data files | I can store it at a location I can easily access |

### Use cases

(For all use cases below, the **System** is `coNnectUS` and the **Actor** is the `user`, unless specified otherwise)

#### Use Case: UC01 - List Contacts

**Actor: User**

**MSS:**
1. User issues list command.
2. coNnectUS displays the list of saved contacts.

    Use case ends.

**Extensions:**

* 1a. coNnectUS detects that the contact list is empty. 
  * 1a1. coNnectUS displays an error message indicating the need to populate the contact list. 
    
    Use case ends.

#### Use Case: UC02 - Add Contacts

**Actor: User**

**MSS:**
1. User adds contact with required contact information.
2. coNnectUS creates the new contact.
3. coNnectUS displays information of new contact and updated contact list.

   Use case ends.

**Extensions:**

* 1a. coNnectUS detects invalid contact information.
    * 1a1. coNnectUS displays an error message, detailing the issue with the user's command.

      Use case resumes from Step 1.
  
* 1b. coNnectUS detects a duplicated contact (Shared phone number/email address).
    * 1b1. coNnectUS displays an error message, indicating whether the phone number, email address or both are already used by another contact.

      Use case ends.

#### Use Case: UC03 - Delete Contacts

**Actor: User**

**MSS:**
1. User <u>views their list of current contacts</u> ([UC01](#use-case-uc01-list-contacts)).
2. User specifies delete command with a list index corresponding to a contact.
3. coNnectUS deletes the contact.
4. coNnectUS displays a status message with the information of the deleted contact, together with the updated contact list.

    Use case ends.

**Extensions:**

* 1a. coNnectUS detects that the currently displayed contact list is empty (i.e. nothing to delete).
    * 1a1. coNnectUS displays an error message informing the user about the empty contact list.

        Use case ends.
  
* 2a. coNnectUS detects an illegal (i.e. missing, zero, negative or non-numeric) list index supplied.
    * 2a1. coNnectUS displays an error message with the expected usage instructions

        Use case resumes from Step 2.

* 2b. coNnectUS detects the positive index supplied is outside the currently displayed contact list.
    * 2b1. coNnectUS displays an error message warning the user of the invalid index. 
  
        Use case resumes from Step 2.

#### Use Case: UC04 - Find Contacts

**Actor: User**

**MSS:**
1. User searches for a contact using any of its fields.
2. coNnectUS returns a list containing the contacts matching all the fields provided. 

   Use case ends.

**Extensions:**

* 1a. coNnectUS detects that no contacts match all the fields provided.
    * 1a1. coNnectUS displays a message informing the user that no contacts were found.
    
      Use case resumes at step 1.

* 1b. coNnectUS detects that no fields were provided. 
    * 1b1. coNnectUS displays an invalid command format message with the expected usage instructions.

      Use case resumes at step 1.

* 1c. coNnectUS detects duplicates of fields other than tags were provided.
    * 1c1. coNnectUS displays a warning message of the duplicated fields.

      Use case resumes at step 1.

* 1d. coNnectUS detects that an unknown field was entered.
    * 1d1. coNnectUS displays a warning message of the unknown field being entered.

      Use case resumes at step 1.

#### Use Case: UC05 - Edit Contacts

**Actor: User**

**MSS**

1. User <u>views their list of current contacts</u> ([UC01](#use-case-uc01-list-contacts)).
2. User requests to edit a specific contact in the list, providing new values for one or more fields.
3. coNnectUS updates the contact with the new values and shows the updated details.

   Use case ends.

**Extensions:**

* 2a. The given index is invalid (e.g. the list is empty or the index is outside the displayed list).

    * 2a1. coNnectUS shows an error message.

      Use case resumes at step 2.

* 2b. No fields to edit are provided.

    * 2b1. coNnectUS shows an error message stating that at least one field must be provided.

      Use case resumes at step 2.

* 2c. One or more of the new values are invalid (e.g. a wrongly formatted phone number or email).

    * 2c1. coNnectUS shows an error message stating the valid format for that field.

      Use case resumes at step 2.

* 2d. The same field is provided more than once (e.g. two names).

    * 2d1. coNnectUS shows an error message listing the repeated fields.

      Use case resumes at step 2.

* 2e. The new phone number or email is the same as that of another contact already in the list.

    * 2e1. coNnectUS shows an error message stating whether the phone number, email address or both are already used by another contact.

      Use case resumes at step 2.

* 2f. User provides new tags.

    * 2f1. coNnectUS replaces all of the contact's existing tags with the new tags.

      Use case resumes at step 3.

* 2g. User provides an empty tag field.

    * 2g1. coNnectUS removes all of the contact's tags.

      Use case resumes at step 3.

* 2h. User provides an empty address field.

    * 2h1. coNnectUS removes the contact's address.

      Use case resumes at step 3.

#### Use Case: UC07 - Clear contacts

**Actor: User**

**MSS:**
1. User specifies the command to clear the contact list.
2. coNnectUS removes all contacts and displays a success message to the user.

   Use case ends.

#### Use Case: UC08 - Pin Contacts

**Actor: User**

**MSS:**
1. User <u>views their list of current contacts</u> ([UC01](#use-case-uc01-list-contacts)).
2. User specifies the command to pin a contact, with an index corresponding to the contact to be pinned.
3. coNnectUS pins the contact, displaying the pinned contact above other unpinned contacts.

   Use case ends.

**Extensions:**

* 2a. coNnectUS detects an illegal or missing list index (e.g. List index is negative, zero, missing or non-numeric).
    * 2a1. coNnectUS displays an error message informing the user of the correct usage of pin command.

      Use case ends.

* 2b. coNnectUS detects an out-of-bounds index (e.g. Currently displayed list has n contacts, user specifies index of n + 1).
    * 2b1. coNnectUS displays an error message informing the user that the provided index is invalid.

      Use case ends.
  
* 2b. coNnectUS detects that the contact at the user-specified index is already pinned.
    * 2b1. coNnectUS displays an error message informing the user that the contact is already pinned.

      Use case ends.


#### Use case: UC09 - Unpin contacts
**Actor: User**

The use case for `UC09` is mostly identical to ([UC08](#use-case-uc08-pin-contacts)), where the user `unpins` a `pinned` contact as opposed to them pinning an unpinned contact.
* When unpinned, the contact will be displayed amongst other unpinned contacts, below any remaining pinned contacts.


#### Use case: UC10 - Sort contacts
**Actor: User**

**MSS:**
1. User specifies the command to sort contacts, with a sorting criterion and sorting order.
2. coNnectUS sorts the contacts and displays a success message.

   Use case ends.

**Extensions:**

* 1a. coNnectUS detects that an invalid sorting criterion or an invalid sorting order was specified.
    * 1a1. coNnectUS displays an error message informing the user that the command was specified incorrectly and show sort command's usage.

      Use case ends.


#### Use Case: UC11 - Create an alias

**Actor: User**

**MSS:**
1. User requests to register an alias for a specified built-in command word.
2. coNnectUS registers the alias for the specified command word and notifies the user that it was successfully registered.

    Use case ends.

**Extensions:**
* 1a. coNnectUS detects that the registration request has an invalid format (e.g. missing arguments or too many arguments).
  * 1a1. coNnectUS displays an error message informing the user of the invalid command format, followed by the command's usage instructions.

    Use case resumes at Step 1.

* 1b. coNnectUS detects that the supplied alias contains characters other than lowercase or uppercase ASCII letters.
  * 1b1. coNnectUS displays an error message informing the user that the alias must contain only the allowed ASCII letters (i.e. `a-z` or `A-Z`).

    Use case resumes at Step 1.

* 1c. coNnectUS detects that an unsupported command word has been supplied (e.g. wrong case for an existing command word, an existing alias or an undefined command word).
  * 1c1. coNnectUS displays an error message informing the user that the supplied command word supplied is unknown.

    Use case resumes at Step 1.

* 1d. coNnectUS detects that the supplied alias conflicts with a built-in command word, ignoring case.
  * 1d1. coNnectUS displays an error message informing the user of a conflict with the existing command word.

    Use case resumes at Step 1.

* 1e. coNnectUS detects that the alias supplied is already registered, using a case-sensitive comparison.
  * 1e1. coNnectUS displays an error message informing the user about the existing alias. 
  
    Use case resumes at Step 1.

#### Use Case: UC12 - List available aliases

**Actor: User**

**MSS:**
1. User requests to view all registered aliases.
2. coNnectUS displays the aliases and their corresponding built-in command words, grouped by command word.

   Use case ends.

**Extensions:**
* 1a. coNnectUS detects that no aliases are currently registered.
    * 1a1. coNnectUS displays an error message informing the user that there are no currently registered aliases.

      Use case ends.

#### Use Case: UC13 - Delete an alias

**Actor: User**

**MSS:**
1. User <u>views their list of available aliases</u> ([UC12](#use-case-uc12-list-available-aliases)).
2. User requests to delete an alias by specifying its name.
3. coNnectUS deletes the specified alias and notifies the user about its successful deletion.

   Use case ends.

**Extensions:**
* 2a. coNnectUS detects that the deletion request has an invalid format (e.g. the alias is missing or the request has too many arguments).
    * 2a1. coNnectUS displays an error message informing the user about the invalid command format, followed by the command's usage instructions.

      Use case resumes at Step 2.

* 2b. coNnectUS detects that the supplied alias contains characters other than lowercase or uppercase ASCII letters.
    * 2b1. coNnectUS displays an error message informing the user that aliases must contain only the allowed ASCII letters (`a-z` or `A-Z`).

      Use case resumes at Step 2.

* 2c. coNnectUS detects that the supplied alias name does not match any registered alias, using a case-sensitive comparison.
    * 2c1. coNnectUS displays an error message informing the user that the alias was not previously registered.

      Use case resumes at Step 2.

### Non-Functional Requirements

1. Should work without an internet connection.
2. Should store data locally in a human-editable text file.
3. Able to operate with 1000 contacts saved without sluggish performance.
4. Should work on any mainstream OS with Java 25 installed.
5. A user who is capable of an average typing speed of 45 WPM should be able to accomplish most tasks faster by typing commands than by using the mouse.
6. Besides Java 25, users should not require additional installations.
7. Error messages should be descriptive such that a well-intentioned user does not make the same mistake more than twice in a row.
8. GUI should be functional and not cause interruptions to the user at most standard screen resolutions (e.g. 1920 x 1080).

### Glossary

* **Alias**: A user-defined, case-sensitive alternative command word that invokes a built-in coNnectUS command.
* **ASCII (American Standard Code for Information Interchange)**: A character-encoding standard containing 128 characters, including letters, digits, punctuation, and control characters.
* **(Built-in) command word**: A predefined, case-sensitive keyword that identifies a supported command in coNnectUS, such as `add`, `find`, or `delete`.
* **Command**: The string of words you enter in order to make use of a feature of the program.
* **(Command-line) Parameters/Arguments/Fields**: The input that you add into commands.
* **(Command) Switch**: An argument modifier added to a command to change how it behaves.
* **Context**: Where/how a user of coNnectUS met the corresponding contact.
* **Domain Name**: Refers to a human-readable string used to visit a website.
* **Export**: Create a data file that contains a number of contacts specified by the user.
* **GUI**: Graphical User Interface / User Interface. Refers to the visual elements with which you interact with to use the program.
* **Import**: Using information from an existing data file to create contacts previously specified.
* **List Index**: The item’s position in the contact list. This always begins at `1`.
* **Mainstream OS**: Windows, Linux, Unix, or macOS.
* **Special Characters**: Symbols on your keyboard that are not alpha-numeric.
* **Tag**: A string label attached to a contact item, as a means to identify attributes related to the contact or group contacts with similar associations.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Changing the theme

1. Test case: `theme light`<br>
   Expected: The window turns white and the theme button shows "Light".

1. Test case: `theme blue`<br>
   Expected: The theme does not change. An error message shows the correct format.

1. Click the theme button, close the app, then relaunch it.<br>
   Expected: The app opens in the theme that was last selected.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Undoing and redoing changes

1. Undoing and redoing a change

   1. Prerequisites: Launch the app with multiple persons in the list.

   1. Test case: `delete 1`, then `undo`<br>
      Expected: The deleted contact is back in the list. All contacts are shown, with pinned contacts at the top.

   1. Test case: `delete 1`, `undo`, then `redo`<br>
      Expected: The contact is deleted again.

   1. Test case: `delete 1`, `list`, then `undo`<br>
      Expected: `list` is skipped, and the deleted contact is back in the list.

1. Nothing to undo or redo

   1. Test case: Relaunch the app, then `undo`<br>
      Expected: The contact list does not change. The status message shows that there are no changes to undo.

   1. Test case: `delete 1`, `undo`, `delete 2`, then `redo`<br>
      Expected: The contact list does not change, since `delete 2` cleared the redo history. The status message shows that there are no undone changes to redo.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
