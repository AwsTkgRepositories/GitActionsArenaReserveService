package com.copel.productpackages.arena.selenium.ota;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;
import com.copel.productpackages.arena.selenium.service.ota.ArenaReservableSearchServiceOta;
import com.copel.productpackages.arena.selenium.service.ota.大田区体育館;

class ArenaReservableSearchServiceOtaTest {

    @Test
    void 大森スポーツセンター空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(true, null, null, 大田区体育館.大森スポーツセンター, CourtUsageType.全面);
        service.execute();
    }

    @Test
    void 田園調布せせらぎ館体育室A空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(true, null, null, 大田区体育館.田園調布せせらぎ館, CourtUsageType.半面1);
        service.execute();
    }

    @Test
    void 田園調布せせらぎ館体育室B空き状況取得() throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(true, null, null, 大田区体育館.田園調布せせらぎ館, CourtUsageType.半面2);
        service.execute();
    }

    @Test
    void 大田区民プラザ体育室() throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(true, null, null, 大田区体育館.大田区民プラザ, CourtUsageType.全面);
        service.execute();
    }

    @Test
    void 大森西区民活動施設体育室() throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(true, null, null, 大田区体育館.大森西区民活動施設, CourtUsageType.全面);
        service.execute();
    }
}
