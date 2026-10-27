package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    private final List<Person> otherPersons = List.of(ALICE, BENSON);

    @Test
    public void getDuplicateContactMessage_samePhoneOnly_returnsPhoneMessage() {
        Person person = new PersonBuilder().withPhone(ALICE.getPhone().value).build();

        assertEquals(Messages.MESSAGE_DUPLICATE_PHONE,
                Messages.getDuplicateContactMessage(person, otherPersons));
    }

    @Test
    public void getDuplicateContactMessage_sameEmailOnly_returnsEmailMessage() {
        Person person = new PersonBuilder().withEmail(ALICE.getEmail().value).build();

        assertEquals(Messages.MESSAGE_DUPLICATE_EMAIL,
                Messages.getDuplicateContactMessage(person, otherPersons));
    }

    @Test
    public void getDuplicateContactMessage_samePhoneAndEmail_returnsPhoneAndEmailMessage() {
        Person person = new PersonBuilder(ALICE).withName("Amy Bee").build();

        assertEquals(Messages.MESSAGE_DUPLICATE_PHONE_AND_EMAIL,
                Messages.getDuplicateContactMessage(person, otherPersons));
    }

    @Test
    public void getDuplicateContactMessage_phoneAndEmailOfDifferentContacts_returnsPhoneAndEmailMessage() {
        Person person = new PersonBuilder()
                .withPhone(ALICE.getPhone().value)
                .withEmail(BENSON.getEmail().value)
                .build();

        assertEquals(Messages.MESSAGE_DUPLICATE_PHONE_AND_EMAIL,
                Messages.getDuplicateContactMessage(person, otherPersons));
    }

    @Test
    public void getDuplicateContactMessage_noSharedField_throwsAssertionError() {
        Person person = new PersonBuilder().build();

        assertThrows(AssertionError.class, () -> Messages.getDuplicateContactMessage(person, otherPersons));
    }
}
