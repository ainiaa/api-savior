package cn.gudqs7.plugins.common.base.postfix.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractPostfixTemplateTest {

    @Test
    void keepsEndOffsetInsideDocumentAndConsumesOptionalSemicolon() {
        assertEquals(3, AbstractPostfixTemplate.removalEndOffset("foo", 3));
        assertEquals(4, AbstractPostfixTemplate.removalEndOffset("foo;", 3));
        assertEquals(3, AbstractPostfixTemplate.removalEndOffset("foo ", 3));
    }
}
