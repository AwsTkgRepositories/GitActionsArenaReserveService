package com.copel.productpackages.arena.selenium.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDateTime;

public class OriginalDateTimeTests {

    private OriginalDateTime originalDateTime;

    @BeforeEach
    public void setUp() {
        originalDateTime = new OriginalDateTime("2025-05-13 14:30:00");
    }

    @Test
    public void testConstructorWithDateString() {
        OriginalDateTime dateTime = new OriginalDateTime("2025-05-13");
        assertNotNull(dateTime);
        assertEquals("2025/05/13", dateTime.getYyyyMMdd());
    }

    @Test
    public void testConstructorWithSqlDate() {
        java.sql.Date sqlDate = java.sql.Date.valueOf("2025-05-13");
        OriginalDateTime dateTime = new OriginalDateTime(sqlDate);
        assertNotNull(dateTime);
        assertEquals("2025/05/13", dateTime.getYyyyMMdd());
    }

    @Test
    public void testConstructorWithTimestamp() {
        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2025-05-13 14:30:00");
        OriginalDateTime dateTime = new OriginalDateTime(timestamp);
        assertNotNull(dateTime);
        assertEquals("2025/05/13", dateTime.getYyyyMMdd());
    }

    @Test
    public void testToString() {
        assertEquals("2025-05-13 14:30:00", originalDateTime.toString());
    }

    @Test
    public void testCompareToEqual() {
        OriginalDateTime dateTime1 = new OriginalDateTime("2025-05-13 14:30:00");
        assertEquals(0, originalDateTime.compareTo(dateTime1));
    }

    @Test
    public void testCompareToGreater() {
        OriginalDateTime dateTime1 = new OriginalDateTime("2025-05-12 14:30:00");
        assertTrue(originalDateTime.compareTo(dateTime1) > 0);
    }

    @Test
    public void testCompareToLesser() {
        OriginalDateTime dateTime1 = new OriginalDateTime("2025-05-14 14:30:00");
        assertTrue(originalDateTime.compareTo(dateTime1) < 0);
    }

    @Test
    public void testIsEmpty() {
        OriginalDateTime emptyDateTime = new OriginalDateTime();
        OriginalDateTime originalDateTime = null;
        emptyDateTime = new OriginalDateTime(originalDateTime);
        assertTrue(emptyDateTime.isEmpty());
    }

    @Test
    public void testGet曜日() {
        assertEquals("(火)", originalDateTime.get曜日());
    }

    @Test
    public void testGetMMdd() {
        assertEquals("05/13", originalDateTime.getMMdd());
    }

    @Test
    public void testGetHHmm() {
        assertEquals("14:30", originalDateTime.getHHmm());
    }

    @Test
    public void testGetHHmmss() {
        assertEquals("14:30:00", originalDateTime.getHHmmss());
    }

    @Test
    public void testGetYyyyMMdd() {
        assertEquals("2025/05/13", originalDateTime.getYyyyMMdd());
    }

    @Test
    public void testGetYyyyMMddWithoutSlash() {
        assertEquals("20250513", originalDateTime.getYyyyMMddWithoutSlash());
    }

    @Test
    public void testGetYyyy_MM_dd() {
        assertEquals("2025-05-13", originalDateTime.getYyyy_MM_dd());
    }

    @Test
    public void testToLocalDateTime() {
        LocalDateTime localDateTime = originalDateTime.toLocalDateTime();
        assertNotNull(localDateTime);
        assertEquals(LocalDateTime.of(2025, 5, 13, 14, 30), localDateTime);
    }

    @Test
    public void testToLocalDate() {
        assertNotNull(originalDateTime.toLocalDate());
    }

    @Test
    public void testToTimestamp() {
        assertNotNull(originalDateTime.toTimestamp());
    }

    @Test
    public void testBetweenDays() {
        OriginalDateTime compareDate = new OriginalDateTime("2025-05-15 14:30:00");
        assertEquals(2, originalDateTime.betweenDays(compareDate));
    }

    @Test
    public void testBetweenMonth() {
        OriginalDateTime compareDate = new OriginalDateTime("2025-07-13 14:30:00");
        assertEquals(2, originalDateTime.betweenMonth(compareDate));
    }

    @Test
    public void testBetweenYear() {
        OriginalDateTime compareDate = new OriginalDateTime("2026-05-13 14:30:00");
        assertEquals(1, originalDateTime.betweenYear(compareDate));
    }

    @Test
    public void testPlusDays() {
        originalDateTime.plusDays(5);
        assertEquals("2025-05-18 14:30:00", originalDateTime.toString());
    }

    @Test
    public void testMinusMinutes() {
        originalDateTime.minusMinutes(30);
        assertEquals("2025-05-13 14:00:00", originalDateTime.toString());
    }

    @Test
    public void testIsAfterHourTrue() {
        assertTrue(originalDateTime.isAfterHour(13));
    }

    @Test
    public void testIsAfterHourFalse() {
        assertFalse(originalDateTime.isAfterHour(15));
    }

    @Test
    public void testSetHour() {
        originalDateTime.setHour(10);
        assertEquals("2025-05-13 10:30:00", originalDateTime.toString());
    }

    @Test
    public void testSetMinute() {
        originalDateTime.setMinute(45);
        assertEquals("2025-05-13 14:45:00", originalDateTime.toString());
    }

    @Test
    public void testEqualsTrue() {
        OriginalDateTime dateTime1 = new OriginalDateTime("2025-05-13 14:30:00");
        assertTrue(originalDateTime.equals(dateTime1));
    }

    @Test
    public void testEqualsFalse() {
        OriginalDateTime dateTime1 = new OriginalDateTime("2025-05-14 14:30:00");
        assertFalse(originalDateTime.equals(dateTime1));
    }
}
