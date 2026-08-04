package cn.gudqs7.plugins.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebEnvironmentUtilTest {

    @Test
    void fallsBackOnlyToProjectRootConfiguration() {
        assertTrue(WebEnvironmentUtil.isProjectRootConfig("/workspace", "/workspace/application.yml"));
        assertFalse(WebEnvironmentUtil.isProjectRootConfig("/workspace", "/workspace/api/application.yml"));
    }
}
