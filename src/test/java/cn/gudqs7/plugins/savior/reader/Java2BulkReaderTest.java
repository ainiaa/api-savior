package cn.gudqs7.plugins.savior.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Java2BulkReaderTest {

    @Test
    void returnsEmptyListForMissingStructure() {
        assertTrue(new Java2BulkReader(null).read(null).isEmpty());
    }
}
