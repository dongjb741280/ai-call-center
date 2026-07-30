package com.voxai.core.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeIdWorkerTest {

    @Test
    void shouldGenerateUniqueIds() {
        SnowflakeIdWorker worker = new SnowflakeIdWorker(1, 1);
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            long id = worker.nextId();
            assertTrue(id > 0);
            assertTrue(ids.add(id), "ID should be unique");
        }
    }

    @Test
    void shouldGenerateIncreasingIds() {
        SnowflakeIdWorker worker = new SnowflakeIdWorker(1, 1);
        long prev = worker.nextId();
        for (int i = 0; i < 100; i++) {
            long next = worker.nextId();
            assertTrue(next > prev);
            prev = next;
        }
    }

    @Test
    void shouldRejectInvalidWorkerId() {
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdWorker(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdWorker(32, 0));
    }

    @Test
    void shouldRejectInvalidDatacenterId() {
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdWorker(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdWorker(0, 32));
    }
}
