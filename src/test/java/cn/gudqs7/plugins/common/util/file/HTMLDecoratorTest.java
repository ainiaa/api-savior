package cn.gudqs7.plugins.common.util.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTMLDecoratorTest {

    @Test
    void escapesHtmlTextAndAttributes() {
        assertEquals("&lt;tag attr=&quot;x&quot;&gt;&amp;", HTMLDecorator.escapeHtml("<tag attr=\"x\">&"));
    }

    @Test
    void rejectsUnsafeLinkProtocolsWhileKeepingRelativePaths() {
        assertEquals("", HTMLDecorator.sanitizeUrl("javascript:alert(1)"));
        assertEquals("docs/page.html", HTMLDecorator.sanitizeUrl("docs/page.html"));
        assertEquals("https://example.com/api?a=1&amp;b=2", HTMLDecorator.sanitizeUrl("https://example.com/api?a=1&b=2"));
    }
}
