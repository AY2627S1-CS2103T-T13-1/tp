package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class TagFactoryTest {

    @Test
    public void create_moduleTag_returnsTrue() throws ParseException {
        assertEquals(new ModuleTag("CS2103"), TagFactory.create("module", "CS2103"));
        assertEquals(new ModuleTag("CS1101S"), TagFactory.create("module", "CS1101S"));
        assertEquals(new ModuleTag("CS3219"), TagFactory.create("module", "CS3219"));
    }

    @Test
    public void create_othersTag_returnsTrue() throws ParseException {
        assertEquals(new OthersTag("friend"), TagFactory.create("others", "friend"));
        assertEquals(new OthersTag("owes money"), TagFactory.create("others", "owes money"));
        assertEquals(new OthersTag("i owe money"), TagFactory.create("others", "i owe money"));
    }

    @Test
    public void create_facultyTag_returnsTrue() throws ParseException {
        assertEquals(new FacultyTag("FOS"), TagFactory.create("faculty", "FOS"));
        assertEquals(new FacultyTag("FASS"), TagFactory.create("faculty", "FASS"));
        assertEquals(new FacultyTag("LAW"), TagFactory.create("faculty", "LAW"));
    }

    @Test
    public void create_unknownTag_throwsParseException() throws ParseException {
        assertThrows(ParseException.class, () -> TagFactory.create("new type", "friend"));
        assertThrows(ParseException.class, () -> TagFactory.create("friends tag", "best friend"));
    }
}
