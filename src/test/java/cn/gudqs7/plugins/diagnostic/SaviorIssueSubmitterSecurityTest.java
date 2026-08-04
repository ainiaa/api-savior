package cn.gudqs7.plugins.diagnostic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SaviorIssueSubmitterSecurityTest {

    @Test
    void shouldNotRegisterAutomaticRemoteErrorReporter() throws IOException {
        String pluginXml = resource("META-INF/plugin.xml");

        assertFalse(pluginXml.contains("<errorHandler"));
    }

    @Test
    void shouldNotPackageGithubIssueSubmitter() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("cn.gudqs7.plugins.diagnostic.SaviorIssueSubmitter"));
    }

    private String resource(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Missing classpath resource: " + path);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
