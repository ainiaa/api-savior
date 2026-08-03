package cn.gudqs7.plugins.common.util.structure;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResolverContextHolderTest {

    @Test
    void 应该_当不同线程解析字段过滤条件时_不共享上下文() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ResolverContextHolder.removeAll();
        try {
            ResolverContextHolder.addData(ResolverContextHolder.HIDDEN_KEYS, Collections.singletonList("secret"));

            assertEquals(Collections.singletonList("secret"), ResolverContextHolder.getData(ResolverContextHolder.HIDDEN_KEYS));
            assertNull(executor.submit(() -> ResolverContextHolder.getData(ResolverContextHolder.HIDDEN_KEYS)).get());
        } finally {
            ResolverContextHolder.removeAll();
            executor.shutdownNow();
        }
    }
}
