package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FacultyTag;
import seedu.address.model.tag.ModuleTag;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_containsAllSamplePersons() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertFalse(sampleAddressBook.getPersonList().isEmpty());
        assertEquals(Arrays.asList(samplePersons), sampleAddressBook.getPersonList());
    }

    @Test
    public void getSampleAddressBook_modifyingOneBook_doesNotAffectAnother() {
        AddressBook firstBook = new AddressBook(SampleDataUtil.getSampleAddressBook());
        ReadOnlyAddressBook secondBook = SampleDataUtil.getSampleAddressBook();
        int originalSize = secondBook.getPersonList().size();
        Person removedPerson = firstBook.getPersonList().getFirst();

        firstBook.removePerson(removedPerson);

        assertEquals(originalSize - 1, firstBook.getPersonList().size());
        assertEquals(originalSize, secondBook.getPersonList().size());
        assertEquals(removedPerson, secondBook.getPersonList().getFirst());
    }

    @Test
    public void getTagSet_noTags_returnsEmptySet() {
        assertEquals(Set.of(), SampleDataUtil.getTagSet());
    }

    @Test
    public void getTagSet_multipleCategories_returnsSuppliedTags() {
        Tag facultyTag = new FacultyTag("Computing");
        Tag moduleTag = new ModuleTag("CS2103T");
        Tag othersTag = new OthersTag("friends");

        Set<Tag> expectedTags = Set.of(facultyTag, moduleTag, othersTag);

        assertEquals(expectedTags,
                SampleDataUtil.getTagSet(facultyTag, moduleTag, othersTag));
    }

    @Test
    public void getTagSet_duplicateTags_returnsUniqueTags() {
        Tag firstTag = new OthersTag("friends");
        Tag duplicateTag = new OthersTag("friends");

        assertEquals(Set.of(firstTag),
                SampleDataUtil.getTagSet(firstTag, duplicateTag));
    }
}