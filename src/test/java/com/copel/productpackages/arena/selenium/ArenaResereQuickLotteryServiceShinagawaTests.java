package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.shinagawa.ArenaResereQuickLotteryServiceShinagawa;
import com.copel.productpackages.arena.selenium.service.shinagawa.品川区体育館;
import com.copel.productpackages.arena.selenium.service.shinagawa.品川区抽選枠;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;

public class ArenaResereQuickLotteryServiceShinagawaTests {

    @Test
    void 品川区体育館早押し予約() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa(false, "", null, "00004348", "Teamreserve0721", 品川区体育館.スクエア荏原, "全面", 品川区抽選枠.午後2);
        service.execute();
    }

    @Test
    void 品川区体育館日付指定早押し予約() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa(new OriginalDate("2025/9/21"), false, "", null, "00004348", "Teamreserve0721", 品川区体育館.スクエア荏原, "全面", 品川区抽選枠.午前);
        service.execute();
    }
}
