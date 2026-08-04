package cn.gudqs7.plugins.savior.savior.more;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class JavaToOneApiSaviorTest {

    @Test
    void acceptsOnlyPositiveOneApiNumbers() {
        assertEquals(12, JavaToOneApiSavior.parsePositiveInteger("12"));
        assertNull(JavaToOneApiSavior.parsePositiveInteger("0"));
        assertNull(JavaToOneApiSavior.parsePositiveInteger("invalid"));
    }

    @Test
    void doesNotMutateSourceListsWhenExpandingMockExamples() {
        Map<String, Object> originalItem = new LinkedHashMap<>();
        originalItem.put("name", "value");
        List<Object> originalList = new ArrayList<>(Collections.singletonList(originalItem));
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("items", originalList);

        new JavaToOneApiSavior(null).getResultExample(source, 2, false);

        assertEquals(1, originalList.size());
        assertSame(originalItem, originalList.get(0));
    }
}
