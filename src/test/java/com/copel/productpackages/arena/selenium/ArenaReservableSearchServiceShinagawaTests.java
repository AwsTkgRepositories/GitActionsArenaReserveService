package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.ArenaReservableSearchServiceShinagawa;
import com.copel.productpackages.arena.selenium.service.entity.unit.品川区体育館;

public class ArenaReservableSearchServiceShinagawaTests {

    @Test
    void 品川区空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceShinagawa service
            = new ArenaReservableSearchServiceShinagawa("", "", 品川区体育館.スクエア荏原);
        service.execute();
    }
}
