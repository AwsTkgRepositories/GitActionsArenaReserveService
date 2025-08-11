package com.copel.productpackages.arena.selenium.setagaya;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.setagaya.ArenaReservableSearchServiceSetagaya;
import com.copel.productpackages.arena.selenium.service.setagaya.世田谷区体育館;

class ArenaReservableSearchServiceSetagayaTests {

    @Test
    void 世田谷区空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceSetagaya service
            = new ArenaReservableSearchServiceSetagaya(true, null, null, 世田谷区体育館.宮坂区民センター);
        service.execute();
    }
}
