package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String COMMAND_DESCRIPTION = "Adds a contact to the address book.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " -n NAME -p PHONE_NUMBER -e EMAIL "
            + "[-a ADDRESS] [-t TAG_CATEGORY TAG_NAME]";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " -n John Doe -p "
            + "98765432 -e johnd@example.com -a 311, Clementi Ave 2, #02-25 "
            + "-t others friends -t others owesMoney";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_PHONE + " PHONE "
            + PREFIX_EMAIL + " EMAIL "
            + PREFIX_ADDRESS + " ADDRESS "
            + "[" + PREFIX_TAG + " CATEGORY TAG]...\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
