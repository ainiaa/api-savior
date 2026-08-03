package cn.gudqs7.plugins.common.util.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void writesUtf8ContentAndCreatesParentDirectory() throws Exception {
        FileUtil.writeStringToFile("中文内容", tempDir.resolve("generated").toFile(), "api.md");

        assertEquals("中文内容", Files.readString(tempDir.resolve("generated/api.md"), StandardCharsets.UTF_8));
    }
}
