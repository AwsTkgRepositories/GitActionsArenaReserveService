package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.shinagawa.ArenaResereQuickLotteryServiceShinagawaAuto;
import com.copel.productpackages.arena.selenium.service.shinagawa.品川区体育館;

public class ArenaResereQuickLotteryServiceShinagawaAutoTests {

    @Test
    void 品川区体育館自動早押し予約() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawaAuto service
            = new ArenaResereQuickLotteryServiceShinagawaAuto(false, "", null, "00004348", "Teamreserve0721", 品川区体育館.スクエア荏原, "全面");
        service.execute();
    }
}
