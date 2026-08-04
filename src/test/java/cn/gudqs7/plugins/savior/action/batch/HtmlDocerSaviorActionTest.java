package cn.gudqs7.plugins.savior.action.batch;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gudqs7.plugins.common.util.file.FreeMarkerUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void escapesUserControlledIndexValues() {
        HtmlDocerSaviorAction.CategoryItem item = new HtmlDocerSaviorAction.CategoryItem(
                "<script>alert(1)</script>", "file", "x' onmouseover='alert(1)", "<b>API</b>");
        HtmlDocerSaviorAction.FileDir fileDir = new HtmlDocerSaviorAction.FileDir();
        fileDir.setFileName("file");
        fileDir.setCategoryItemList(Collections.singletonList(item));
        HtmlDocerSaviorAction.Module module = new HtmlDocerSaviorAction.Module();
        module.setModuleName(item.getModuleName());
        module.setFileDirList(Collections.singletonList(fileDir));

        Map<String, Object> model = new HashMap<>();
        model.put("moduleList", Collections.singletonList(module));
        String html = FreeMarkerUtil.renderTemplate("html/index.ftl", model);

        assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertTrue(html.contains("&lt;b&gt;API&lt;/b&gt;"));
        assertFalse(html.contains("onmouseover='alert(1)"));
    }
}
