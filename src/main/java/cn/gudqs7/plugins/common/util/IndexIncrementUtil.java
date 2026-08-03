package cn.gudqs7.plugins.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wq
 */
public class IndexIncrementUtil {

    private static final ThreadLocal<AtomicInteger> INDEX = ThreadLocal.withInitial(AtomicInteger::new);

    public static int getIndex() {
        return INDEX.get().getAndIncrement();
    }

    public static void clear() {
        INDEX.remove();
    }

}
