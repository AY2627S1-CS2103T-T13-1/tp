package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

public class JsonSerializableAliasesTest {

    private static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of("find", "list");

    @Test
    public void toLoadResult_emptyAliases_returnsEmptyRegistry() {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of());
        assertEquals(Map.of(), snapshot.toLoadResult(BUILT_IN_COMMAND_WORDS).aliases().getAliases());
    }

    @Test
    public void toLoadResult_sourceMapModified_preservesOriginalAliases() {
        Map<String, String> expected = Map.of("f", "find", "L", "list");
        Map<String, String> aliases = new HashMap<>(expected);
        JsonSerializableAliases snapshot = new JsonSerializableAliases(aliases);
        aliases.clear();

        assertEquals(expected, snapshot.toLoadResult(BUILT_IN_COMMAND_WORDS).aliases().getAliases());
    }

    @Test
    public void toLoadResult_nullAliasOrCommandWord_skipsInvalidEntries() {
        Map<String, String> aliases = new HashMap<>();
        aliases.put(null, "find");
        aliases.put("f", null);
        aliases.put("L", "list");
        JsonSerializableAliases snapshot = new JsonSerializableAliases(aliases);

        assertEquals(Map.of("L", "list"), snapshot.toLoadResult(BUILT_IN_COMMAND_WORDS).aliases().getAliases());
    }

    @Test
    public void toLoadResult_unsupportedCommandWord_skipsInvalidEntry() {
        JsonSerializableAliases snapshot = new JsonSerializableAliases(Map.of("f", "unknown", "L", "list"));

        AliasLoadResult result = snapshot.toLoadResult(BUILT_IN_COMMAND_WORDS);

        assertEquals(Map.of("L", "list"), result.aliases().getAliases());
        assertEquals(List.of("Skipped alias entry 'f': Unsupported command word unknown"),
                result.warnings());
    }

    @Test
    public void fromJson_nullOrMissingAliases_throwsIllegalArgumentException() {
        String expectedMessage = "Missing required property 'aliases'. "
                + "Expected an object such as {\"aliases\":{\"f\":\"find\"}}.";
        for (JsonNode aliases : new JsonNode[] {null, MissingNode.getInstance()}) {
            assertThrows(IllegalArgumentException.class, expectedMessage, () ->
                    JsonSerializableAliases.fromJson(aliases));
        }
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
