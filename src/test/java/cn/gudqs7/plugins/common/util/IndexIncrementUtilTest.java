package cn.gudqs7.plugins.common.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexIncrementUtilTest {

    @Test
    void 应该_当不同线程生成字段序号时_各自从零开始() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        IndexIncrementUtil.clear();
        try {
            assertEquals(0, IndexIncrementUtil.getIndex());
            assertEquals(0, executor.submit(IndexIncrementUtil::getIndex).get());
        } finally {
            IndexIncrementUtil.clear();
            executor.shutdownNow();
        }
    }
}
