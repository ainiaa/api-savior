package cn.gudqs7.plugins.common.resolver;

import cn.gudqs7.plugins.common.enums.HttpMethod;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMappingResolverTest {

    @Test
    void joinsControllerAndMethodPathsWithoutDuplicateSeparators() {
        assertEquals("/api/users", RequestMappingResolver.joinPaths("/api/", "/users"));
        assertEquals("/users", RequestMappingResolver.joinPaths("", "users"));
        assertEquals("/", RequestMappingResolver.joinPaths("", ""));
    }

    @Test
    void resolvesPatchMappingAsPatchMethod() {
        assertEquals(HttpMethod.PATCH,
                RequestMappingResolver.fixedMethod(AnnotationHolder.QNAME_OF_PATCH_MAPPING));
    }
}
