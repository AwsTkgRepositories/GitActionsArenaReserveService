package com.copel.productpackages.arena.selenium.setagaya;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.setagaya.ArenaReservableSearchServiceSetagaya;

class ArenaReservableSearchServiceSetagayaTests {

    @Test
    void 世田谷区空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceSetagaya service
            = new ArenaReservableSearchServiceSetagaya(false, null, null, null);
        service.execute();
    }
}
