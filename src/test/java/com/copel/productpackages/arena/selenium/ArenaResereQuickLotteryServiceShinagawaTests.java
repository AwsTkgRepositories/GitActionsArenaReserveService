package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.ArenaResereQuickLotteryServiceShinagawa;
import com.copel.productpackages.arena.selenium.service.entity.unit.品川区体育館;
import com.copel.productpackages.arena.selenium.service.entity.unit.品川区抽選枠;

public class ArenaResereQuickLotteryServiceShinagawaTests {

    @Test
    void 品川区体育館早押し取得() throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa(false, "", "", "00004348", "Teamreserve0721", 品川区体育館.スクエア荏原, "全面", 品川区抽選枠.午後2);
        service.execute();
    }
}
