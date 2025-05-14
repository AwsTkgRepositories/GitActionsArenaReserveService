package com.copel.productpackages.arena.selenium.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlot;
import com.copel.productpackages.arena.selenium.service.entity.TimeSlot;
import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDateTime;
import com.copel.productpackages.arena.selenium.service.entity.unit.ReserveStatus;

class ReservationSlotTests {

    private ReservationSlot reservationSlot;
    private OriginalDate slotDate;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        slotDate = new OriginalDate(LocalDate.of(2025, 5, 10));  // 土曜日
        OriginalDateTime start = new OriginalDateTime("2022-10-01 10:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 11:00:00");
        TimeSlot timeSlot = new TimeSlot("昼間", start, end);
        reservationSlot = new ReservationSlot();
        reservationSlot.setSlotDate(slotDate);
        reservationSlot.setTimeSlot(timeSlot);
        reservationSlot.setUsageType(CourtUsageType.全面);
    }

    @Test
    void testIs予約可能() {
        reservationSlot.setReserveStatus(ReserveStatus.予約可能);
        assertTrue(reservationSlot.is予約可能(), "予約可能であるべき");

        reservationSlot.setReserveStatus(ReserveStatus.予約不可);
        assertFalse(reservationSlot.is予約可能(), "予約不可であるべき");
    }

    @Test
    void testIs予約閲覧対象() {
        reservationSlot.setReserveStatus(ReserveStatus.予約可能);
        assertTrue(reservationSlot.is予約閲覧対象(), "予約閲覧対象であるべき");

        reservationSlot.setReserveStatus(ReserveStatus.その他);
        assertFalse(reservationSlot.is予約閲覧対象(), "予約閲覧対象でないべき");
    }

    @Test
    void testIs土日祝日() {
        // 土曜日の場合
        assertTrue(reservationSlot.is土日祝日(), "土日祝日であるべき");

        // 平日を設定して確認
        reservationSlot.setSlotDate(new OriginalDate(LocalDate.of(2025, 5, 12))); // 月曜日
        assertFalse(reservationSlot.is土日祝日(), "平日であるべき");
    }

    @Test
    void testIs平日夜間() {
        // 平日昼間の場合
        assertFalse(reservationSlot.is平日夜間(), "平日昼間であるべき");

        // 平日夜間に変更
        reservationSlot.setSlotDate(new OriginalDate(LocalDate.of(2025, 5, 12))); // 月曜日
        OriginalDateTime start = new OriginalDateTime("2022-10-01 18:00:00");
        OriginalDateTime end = new OriginalDateTime("2022-10-01 21:00:00");
        TimeSlot timeSlot = new TimeSlot("夜間", start, end);
        reservationSlot.setTimeSlot(timeSlot); // 夜間
        assertTrue(reservationSlot.is平日夜間(), "平日夜間であるべき");
    }

    @Test
    void testToString() {
        reservationSlot.setReserveStatus(ReserveStatus.予約可能);
        String expected = "5/10(土)：昼間(10:00〜11:00)全面 ○";
        assertEquals(expected, reservationSlot.toString(), "toStringの出力が期待と異なります");
    }

    @Test
    void testSetGetSlotDate() {
        reservationSlot.setSlotDate(slotDate);
        assertEquals(slotDate, reservationSlot.getSlotDate(), "slotDateの取得が正しくない");
    }

    @Test
    void testSetGetTimeSlot() {
        reservationSlot.setTimeSlot(timeSlot);
        assertEquals(timeSlot, reservationSlot.getTimeSlot(), "timeSlotの取得が正しくない");
    }

    @Test
    void testSetGetUsageType() {
        reservationSlot.setUsageType(CourtUsageType.全面);
        assertEquals(CourtUsageType.全面, reservationSlot.getUsageType(), "usageTypeの取得が正しくない");
    }

    @Test
    void testSetGetReserveStatus() {
        reservationSlot.setReserveStatus(ReserveStatus.予約可能);
        assertEquals(ReserveStatus.予約可能, reservationSlot.getReserveStatus(), "reserveStatusの取得が正しくない");
    }
}
