package com.copel.productpackages.arena.selenium;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.copel.productpackages.arena.selenium.service.ArenaReservableSearchServiceShinagawa;

public class ArenaReservableSearchServiceShinagawaTests {
    @Test
    void executeTest() throws InterruptedException, IOException {
        ArenaReservableSearchServiceShinagawa service
            = new ArenaReservableSearchServiceShinagawa("", "");
        service.execute();
    }
}
