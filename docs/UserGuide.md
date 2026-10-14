---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# coNnectUS User Guide

coNnectUS is a **desktop Address Book application designed for NUS students** to manage their contacts. It is **optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, coNnectUS can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-T13-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press <kbd>Enter</kbd> to view the commands available.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add -n John Doe -p 98765432 -e johnd@example.com -a John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add -n NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `-n NAME [-t CATEGORY TAG]` can be used as `-n John Doe -t others friend` or as `-n John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[-t CATEGORY TAG]... ` may be omitted, or written as `-t others friend` or `-t others friend -t others family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `-n NAME -p PHONE_NUMBER`, `-p PHONE_NUMBER -n NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Displays the list of commands, their usages, descriptions, examples and the link for the User Guide.

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add -n NAME -p PHONE_NUMBER -e EMAIL [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]... `

<box type="tip" seamless>

**Tip:** A person can have any number of tags, including zero.

**Tip:** The address is optional.
</box>

Examples:
* `add -n John Doe -p 98765432 -e johnd@example.com -a John street, block 123, #01-01`
* `add -n Betsy Crowe -t others friend -e betsycrowe@example.com -a Newgate Prison -p 1234567 -t others criminal`
* `add -n Jane Tan -p 87654321 -e janetan@example.com`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]... `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `-t` without a tag after it.
* To remove a person's address, enter `-a` without an address after it.

Examples:
*  `edit 1 -p 91234567 -e johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 -n Betsy Crower -t` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.
*  `edit 3 -a` Removes the address of the 3rd person.

### Locating persons by criteria: `find`

Finds persons in the address book matching the supplied criteria.

Format: `find [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]...`

* At least one criterion must be specified.
* Multiple tags can be supplied, but other parameters (name, phone, email, address) may only be specified once.
* Every criterion supplied must match to find a person.
* Search criteria are case-insensitive. For example, `-n hans` matches `-n Hans`.
* Persons are matched if the search criterion is a substring of the actual field; for example `-n John` matches `John` and `Johnny`.
* For tag criteria, both a valid category (i.e. `module`, `faculty` or `others`) and the tag name must be supplied.

Examples:
* `find -n john` returns `John`, `Johnny`.
* `find -e alice` returns `alice@example.com`, `alicelow@gmail.com`
* `find -t others Friend -t faculty Computing` returns persons who have both tags.<br>
  ![result for 'find -t others Friend -t faculty Computing'](images/findMultipleTagsResult.png)

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find -n Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the User Guide Window** and then use the `User Guide` menu, or the keyboard shortcut `F1` again, the original User Guide Window may remain minimized, and no new User Guide Window will appear. The remedy is to manually restore the minimized User Guide Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add -n NAME -p PHONE_NUMBER -e EMAIL [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]... ` <br> e.g., `add -n James Ho -p 22224444 -e jamesho@example.com -a 123, Clementi Rd, 1234665 -t others friend -t others colleague`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]... `<br> e.g.,`edit 2 -n James Lee -e jameslee@example.com`
**Find**   | `find [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]... `<br> e.g.,`find -n James -p 2222 -t others friend`
**List**   | `list`
**Help**   | `help`
