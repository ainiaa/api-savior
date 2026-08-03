package cn.gudqs7.plugins.common.resolver.comment;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PsiMethodAnnotationHolderImplTest {

    @Test
    void retainsPlainParametersAndRemovesKnownMethodParameterPrefix() {
        assertEquals(Arrays.asList("id", "userId", "filter.name"),
                PsiMethodAnnotationHolderImpl.normalizeRequestParameters(
                        Arrays.asList("request.id", "userId", "filter.name"),
                        new HashSet<>(Arrays.asList("request", "userId"))));
    }
}
