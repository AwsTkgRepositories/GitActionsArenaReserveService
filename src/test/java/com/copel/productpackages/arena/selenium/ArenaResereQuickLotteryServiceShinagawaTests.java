package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.ArenaResereQuickLotteryServiceShinagawa;

public class ArenaResereQuickLotteryServiceShinagawaTests {
    @Test
    void executeTest() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa("", "");
        service.execute();
    }
}
