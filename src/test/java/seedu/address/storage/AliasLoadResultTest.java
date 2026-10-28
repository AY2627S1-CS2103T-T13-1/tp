package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.alias.AliasRegistry;

public class AliasLoadResultTest {

    @Test
    public void loadedAliasCount_emptyAndPopulatedRegistries_returnsAliasCount() {
        AliasRegistry registry = new AliasRegistry(Set.of("find"));
        assertEquals(0, new AliasLoadResult(registry, List.of()).loadedAliasCount());

        registry.register("find", "f");
        assertEquals(1, new AliasLoadResult(registry, List.of()).loadedAliasCount());

        registry.register("find", "search");
        assertEquals(2, new AliasLoadResult(registry, List.of("Skipped an invalid entry.")).loadedAliasCount());
    }
}
