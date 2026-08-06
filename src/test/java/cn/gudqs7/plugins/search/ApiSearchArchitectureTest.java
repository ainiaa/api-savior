package cn.gudqs7.plugins.search;

import cn.gudqs7.plugins.search.resolver.ApiResolverService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiSearchArchitectureTest {

    @Test
    void doesNotKeepStaleItemsInSearchContributor() {
        assertThrows(NoSuchFieldException.class,
                () -> ApiSearchContributor.class.getDeclaredField("navItemList"));
    }

    @Test
    void boundsComposedControllerDiscovery() throws Exception {
        Field field = ApiResolverService.class.getDeclaredField("MAX_COMPOSED_CONTROLLER_DEPTH");
        field.setAccessible(true);
        assertEquals(8, field.getInt(null));
    }
}
