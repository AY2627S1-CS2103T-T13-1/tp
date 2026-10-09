package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TagSubclassTest {

    @Test
    public void equals_differentTagType_returnsFalse() {
        assertNotEquals(new ModuleTag("CS2103"), new FacultyTag("SOC"));
        assertNotEquals(new OthersTag("friend"), new FacultyTag("FASS"));
        assertNotEquals(new ModuleTag("CS2105"), new OthersTag("bob's friend"));
    }

    @Test
    public void equals_notTag_returnsFalse() {
        assertNotEquals(new ModuleTag("CS2103"), "CS2103");
        assertNotEquals(new OthersTag("friend"), "friend");
        assertNotEquals(new FacultyTag("FASS"), "FASS");
    }

    @Test
    public void getTagTypeMethod() {
        assertEquals(new ModuleTag("CS2103").getTagCategory(), "module");
        assertEquals(new FacultyTag("FOS").getTagCategory(), "faculty");
        assertEquals(new OthersTag("friend").getTagCategory(), "others");

    }

    @Test
    public void equals_sameTagTypeDifferentTagName_returnsFalse() {
        assertNotEquals(new ModuleTag("CS2103"), new ModuleTag("CS2109"));
        assertNotEquals(new ModuleTag("IS2218"), new ModuleTag("IS2238"));
        assertNotEquals(new ModuleTag("IS4238"), new ModuleTag("CS4238"));

        assertNotEquals(new OthersTag("friend"), new OthersTag("mortal enemy"));
        assertNotEquals(new OthersTag("other friend"), new OthersTag("sus"));
        assertNotEquals(new OthersTag("lives in east"), new OthersTag("lives in west"));

        assertNotEquals(new FacultyTag("FOS"), new FacultyTag("SOC"));
        assertNotEquals(new FacultyTag("CDE"), new FacultyTag("FASS"));
        assertNotEquals(new FacultyTag("BIZ"), new FacultyTag("LAW"));
    }

    @Test
    public void equals_sameTagTypeSameTagName_returnsTrue() {
        assertEquals(new ModuleTag("CS2103"), new ModuleTag("CS2103"));
        assertEquals(new OthersTag("friend"), new OthersTag("friend"));
        assertEquals(new FacultyTag("SOC"), new FacultyTag("SOC"));
    }
}
