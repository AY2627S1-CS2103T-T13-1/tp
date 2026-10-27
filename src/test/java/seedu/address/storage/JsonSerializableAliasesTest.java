package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializableAliasesTest {

    private static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of("find", "list");

    @Test
    public void toModelType_emptyAliases_returnsEmptyRegistry() {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of());
        assertEquals(Map.of(), snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void toModelType_sourceMapModified_preservesOriginalAliases() {
        Map<String, String> expected = Map.of("f", "find", "L", "list");
        Map<String, String> aliases = new HashMap<>(expected);
        JsonSerializableAliases snapshot = new JsonSerializableAliases(aliases);
        aliases.clear();

        assertEquals(expected, snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void toModelType_nullAliasOrCommandWord_skipsInvalidEntries() {
        Map<String, String> aliases = new HashMap<>();
        aliases.put(null, "find");
        aliases.put("f", null);
        aliases.put("L", "list");
        JsonSerializableAliases snapshot = new JsonSerializableAliases(aliases);

        assertEquals(Map.of("L", "list"), snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void toModelType_unsupportedCommandWord_skipsInvalidEntry() {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of("f", "unknown", "L", "list"));

        assertEquals(Map.of("L", "list"), snapshot.toModelType(BUILT_IN_COMMAND_WORDS).getAliases());
    }

    @Test
    public void fromJson_nullAliases_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> JsonSerializableAliases.fromJson(null));
    }

    @Test
    public void fromJson_nonObjectAliases_throwsIllegalArgumentException() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        for (String json : new String[]{"null", "[]", "\"list\""}) {
            JsonNode aliases = mapper.readTree(json);

            assertThrows(IllegalArgumentException.class, () -> JsonSerializableAliases.fromJson(aliases));
        }
    }
}
