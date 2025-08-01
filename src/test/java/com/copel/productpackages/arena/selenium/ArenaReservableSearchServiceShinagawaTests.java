package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.shinagawa.ArenaReservableSearchServiceShinagawa;
import com.copel.productpackages.arena.selenium.service.shinagawa.品川区体育館;

public class ArenaReservableSearchServiceShinagawaTests {

    @Test
    void 品川区空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceShinagawa service
            = new ArenaReservableSearchServiceShinagawa("", null, 品川区体育館.スクエア荏原);
        service.execute();
    }
}
