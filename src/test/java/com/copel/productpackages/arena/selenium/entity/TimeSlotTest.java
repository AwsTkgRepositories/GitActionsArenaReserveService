package com.copel.productpackages.arena.selenium.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.entity.TimeSlot;
import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDateTime;


public class TimeSlotTest {

    @Test
    void test夜間_trueの場合() {
        OriginalDateTime start = new OriginalDateTime("2022-10-01 19:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 21:00:00");
        TimeSlot timeSlot = new TimeSlot("夜間枠", start, end);
        assertTrue(timeSlot.is夜間());
    }

    @Test
    void test夜間_falseの場合() {
        OriginalDateTime start = new OriginalDateTime("2022-10-01 10:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 11:00:00");
        TimeSlot timeSlot = new TimeSlot("午前枠", start, end);
        assertFalse(timeSlot.is夜間());
    }

    @Test
    void testToDisplayString() {
        OriginalDateTime start = new OriginalDateTime("2022-10-01 10:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 11:00:00");
        TimeSlot timeSlot = new TimeSlot("午前枠", start, end);
        assertEquals("午前枠(10:00〜11:00)", timeSlot.toDisplayString());
    }

    @Test
    void testGetStartAndEndTime() {
        OriginalDateTime start = new OriginalDateTime("2022-10-01 10:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 11:00:00");
        TimeSlot timeSlot = new TimeSlot("午前枠", start, end);
        assertEquals(start, timeSlot.getStartTime());
        assertEquals(end, timeSlot.getEndTime());
    }
}
