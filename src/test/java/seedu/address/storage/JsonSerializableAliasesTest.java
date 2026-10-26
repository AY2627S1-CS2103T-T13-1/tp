package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonSerializableAliasesTest {

    private static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of("find", "list");

    @Test
    public void toModelType_emptyAliases_returnsEmptyRegistry() throws IllegalValueException {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of());
        assertEquals(Map.of(), snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void toModelType_sourceMapModified_preservesOriginalAliases() throws IllegalValueException {
        Map<String, String> expected = Map.of("f", "find", "L", "list");
        Map<String, String> aliases = new HashMap<>(expected);
        JsonSerializableAliases snapshot = new JsonSerializableAliases(aliases);
        aliases.clear();

        assertEquals(expected, snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void toModelType_nullAliasOrCommandWord_throwsIllegalValueException() {
        Map<String, String> aliases = new HashMap<>();
        aliases.put(null, "find");
        JsonSerializableAliases nullAlias = new JsonSerializableAliases(aliases);

        assertThrows(IllegalValueException.class, "Alias and command words cannot be null!", () ->
                nullAlias.toModelType(BUILT_IN_COMMAND_WORDS));

        aliases.clear();
        aliases.put("f", null);
        JsonSerializableAliases nullCommandWord = new JsonSerializableAliases(aliases);

        assertThrows(IllegalValueException.class, "Alias and command words cannot be null!", () ->
                nullCommandWord.toModelType(BUILT_IN_COMMAND_WORDS));
    }

    @Test
    public void toModelType_unsupportedCommandWord_throwsIllegalValueException() {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of("f", "unknown"));

        assertThrows(IllegalValueException.class,
                "Invalid alias entry 'f': Unsupported command word unknown", () ->
                snapshot.toModelType(BUILT_IN_COMMAND_WORDS));
    }
}
