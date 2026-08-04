package cn.gudqs7.plugins.common.resolver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMappingResolverTest {

    @Test
    void joinsControllerAndMethodPathsWithoutDuplicateSeparators() {
        assertEquals("/api/users", RequestMappingResolver.joinPaths("/api/", "/users"));
        assertEquals("/users", RequestMappingResolver.joinPaths("", "users"));
        assertEquals("/", RequestMappingResolver.joinPaths("", ""));
    }
}
