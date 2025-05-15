package com.copel.productpackages.arena.selenium.service;

import java.io.IOException;

import com.copel.productpackages.arena.selenium.service.entity.unit.品川区体育館;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;

import lombok.extern.slf4j.Slf4j;

/**
 * 品川区の毎月の抽選申し込み処理クラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class ArenaResereMonthlyLotteryServiceShinagawa {
    /**
     * 環境変数.
     * 通知用LINEアカウントのチャンネルアクセストークン.
     */
    private static String LINE_CHANNEL_ACCESS_TOKEN = System.getenv("LINE_CHANNEL_ACCESS_TOKEN");
    /**
     * 環境変数.
     * 通知の宛先LINE ID.
     */
    private static String NOTIFY_LINE_ID = System.getenv("NOTIFY_LINE_ID");
    /**
     * 環境変数.
     * 品川区の団体登録ID.
     */
    private static String ACCOUNT_ID = System.getenv("ACCOUNT_ID");
    /**
     * 環境変数.
     * 品川区の団体登録パスワード.
     */
    private static String ACCOUNT_PASSWORD = System.getenv("ACCOUNT_PASSWORD");
    /**
     * 環境変数.
     * GitHUB Actionsに入力された対象体育館名.
     */
    private static String TARGET_ARENA_NAME = System.getenv("TARGET_ARENA_NAME");

    /**
     * WebBrowserクライアント.
     */
    private WebBrowser webBrowser;
    /**
     * 結果の送信先LINE ID.
     */
    private String toLineId;
    /**
     * チャンネルアクセストークン.
     */
    private String channelAccessToken;
    /**
     * 品川区の団体登録ID.
     */
    private String accountId;
    /**
     * 品川区の団体登録パスワード.
     */
    private String accountPassword;
    /**
     * 予約対象の体育館.
     */
    private 品川区体育館 targetArena;

    /**
     * コンストラクタ.
     */
    public ArenaResereMonthlyLotteryServiceShinagawa(final String channelAccessToken, final String toLineId,final String accountId, final String accountPassword, final 品川区体育館 targetArena) {
        this.webBrowser = new WebBrowser(true);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
    }
    public ArenaResereMonthlyLotteryServiceShinagawa(final boolean isHeadlessMode, final String channelAccessToken, final String toLineId,final String accountId, final String accountPassword, final 品川区体育館 targetArena) {
        this.webBrowser = new WebBrowser(isHeadlessMode);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
    }

    /**
     * 処理.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void execute() throws InterruptedException, IOException {
        // (0) URL先へアクセスする
        this.webBrowser.access("https://www.cm9.eprs.jp/shinagawa/web/");

        // ドライバを閉じる
        this.webBrowser.quit();
    }
}
