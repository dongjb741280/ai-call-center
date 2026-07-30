package com.voxai.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    @Test
    void shouldFormatTimestamp() {
        long timestamp = 1700000000000L;
        String result = DateTimeUtil.format(timestamp);
        assertNotNull(result);
        assertTrue(result.contains("2023"));
    }

    @Test
    void shouldReturnEmptyForNullTimestamp() {
        assertEquals("", DateTimeUtil.format(null));
        assertEquals("", DateTimeUtil.format(0L));
    }

    @Test
    void shouldFormatWithCustomPattern() {
        long timestamp = 1700000000000L;
        String result = DateTimeUtil.format(timestamp, DateTimeUtil.YYYYMMDD);
        assertNotNull(result);
        assertEquals(8, result.length());
    }

    @Test
    void shouldGetStartTime() {
        Long startTime = DateTimeUtil.getStartTime();
        assertNotNull(startTime);
        assertTrue(startTime > 0);
    }

    @Test
    void shouldGetEndTime() {
        Long endTime = DateTimeUtil.getEndTime();
        assertNotNull(endTime);
        assertTrue(endTime > 0);
    }

    @Test
    void shouldGetTodayTime() {
        java.util.Map map = DateTimeUtil.getTodayTime();
        assertNotNull(map);
        assertTrue(map.containsKey("startTime"));
        assertTrue(map.containsKey("endTime"));
        assertTrue((Long) map.get("startTime") <= (Long) map.get("endTime"));
    }

    @Test
    void shouldDetectToday() {
        long now = System.currentTimeMillis();
        assertTrue(DateTimeUtil.isToday(now));
    }

    @Test
    void shouldFormatCurrentDate() {
        String result = DateTimeUtil.format();
        assertNotNull(result);
        assertEquals(8, result.length());
    }

    @Test
    void shouldAddDays() {
        long now = System.currentTimeMillis();
        Long future = DateTimeUtil.addday(new java.util.Date(), 7);
        assertTrue(future > now);
    }

    @Test
    void shouldGetCallTime() {
        SnowflakeIdWorker worker = new SnowflakeIdWorker(1, 1);
        long id = worker.nextId();
        Long callTime = DateTimeUtil.getCallTime(id);
        assertNotNull(callTime);
        assertTrue(callTime > 0);
    }
}
