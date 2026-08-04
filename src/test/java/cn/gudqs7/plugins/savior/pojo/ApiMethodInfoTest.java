package cn.gudqs7.plugins.savior.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiMethodInfoTest {

    @Test
    void 应该_当创建接口语义模型时_保留解析结果() {
        ApiMethodInfo methodInfo = new ApiMethodInfo(null, "demo.Api", null, null, null, null);

        assertEquals("demo.Api", methodInfo.getInterfaceClassName());
        assertNull(methodInfo.getProject());
        assertNull(methodInfo.getPublicMethod());
        assertNull(methodInfo.getCommentInfo());
        assertNull(methodInfo.getParamStructureAndCommentInfo());
        assertNull(methodInfo.getReturnStructureAndCommentInfo());
    }
}
