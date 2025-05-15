package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.ArenaResereQuickLotteryServiceShinagawa;
import com.copel.productpackages.arena.selenium.service.entity.unit.品川区抽選枠;

public class ArenaResereQuickLotteryServiceShinagawaTests {

    @Test
    void 品川区体育館早押し取得() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa("", "", "00004348", "Teamreserve0721", "スクエア荏原", "全面", 品川区抽選枠.夜間);
        service.execute();
    }
}
