package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons matching every supplied search criterion.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String COMMAND_DESCRIPTION = "Finds contacts that match the supplied search"
            + " criteria (i.e. name, tag, phone number, email address and/or address) and displays"
            + " them as a list with index numbers.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_TAG + " TAG_CATEGORY TAG_NAME] "
            + "[" + PREFIX_PHONE + " PHONE_NUMBER] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS]";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + " Johnny "
            + PREFIX_TAG + " others coder";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PHONE + " PHONE] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " CATEGORY TAG]...\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_NO_CRITERION = "Please provide at least one criterion to find.";
    public static final String MESSAGE_CONTACTS_FOUND = "%1$d contact(s) found.";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(MESSAGE_CONTACTS_FOUND, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
