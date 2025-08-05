package com.copel.productpackages.arena.selenium.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;

class OriginalDateTests {

    @Test
    void testIs祝日() {
        OriginalDate originalDate = new OriginalDate("2025/07/21");
        assertTrue(originalDate.is祝日());
        originalDate = new OriginalDate("2026/10/12");
        assertTrue(originalDate.is祝日());
    }
}
