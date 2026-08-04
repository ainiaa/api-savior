package cn.gudqs7.plugins.savior.action.batch;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlDocerSaviorActionTest {

    @Test
    void keepsCategoryOrderInGeneratedIndex() {
        List<HtmlDocerSaviorAction.CategoryItem> items = Arrays.asList(
                new HtmlDocerSaviorAction.CategoryItem("module-b", "B", "b", "b"),
                new HtmlDocerSaviorAction.CategoryItem("module-a", "A", "a", "a"),
                new HtmlDocerSaviorAction.CategoryItem("module-b", "C", "c", "c")
        );

        Map<String, List<HtmlDocerSaviorAction.CategoryItem>> grouped = HtmlDocerSaviorAction.groupByModule(items);

        assertEquals(Arrays.asList("module-b", "module-a"), Arrays.asList(grouped.keySet().toArray(new String[0])));
        assertEquals(Arrays.asList("B", "C"), Arrays.asList(
                grouped.get("module-b").get(0).getFileName(),
                grouped.get("module-b").get(1).getFileName()
        ));
    }
}
