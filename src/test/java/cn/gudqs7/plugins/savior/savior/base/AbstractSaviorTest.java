package cn.gudqs7.plugins.savior.savior.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractSaviorTest {

    @Test
    void comparesExtremeOrderValuesWithoutOverflow() {
        assertTrue(AbstractSavior.compareOrderValues(Integer.MIN_VALUE, Integer.MAX_VALUE) < 0);
        assertTrue(AbstractSavior.compareOrderValues(Integer.MAX_VALUE, Integer.MIN_VALUE) > 0);
        assertEquals(0, AbstractSavior.compareOrderValues(10, 10));
    }
}
