package com.copel.productpackages.arena.selenium.service;

import java.io.IOException;

import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.entity.unit.品川区抽選枠;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;

import lombok.extern.slf4j.Slf4j;

/**
 * 品川区の体育館空き状況の検索処理クラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class ArenaResereQuickLotteryServiceShinagawa {
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
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa(LINE_CHANNEL_ACCESS_TOKEN, NOTIFY_LINE_ID, 品川区抽選枠.夜間);
        service.execute();
    }
        
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
     * 予約対象の枠.
     */
    private 品川区抽選枠 予約対象;

    /**
     * コンストラクタ.
     */
    public ArenaResereQuickLotteryServiceShinagawa(final String channelAccessToken, final String toLineId, final 品川区抽選枠 予約対象) {
        this.webBrowser = new WebBrowser(false);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.予約対象 = 予約対象;
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

        // 2ヵ月後同日の日付を作成
        OriginalDate dateAfterTwoMonth = new OriginalDate();
        dateAfterTwoMonth.plusMonths(2);

        try {
            // (1) 検索条件画面
            // 検索条件「いつ」を選択
            this.webBrowser.clickByXpath("//*[@id=\"collapse-when\"]");
            log.info("「いつ」プルダウンを押下");

            // 開始日を入力
            this.webBrowser.inputDateByXpath("//*[@id=\"daystart\"]", dateAfterTwoMonth.toYYYYMMDDwithHyphen());
            log.info("「開始日」を入力");

            // 検索条件「どこで」を選択
            this.webBrowser.selectOptionByXpath("//*[@id=\"bname\"]", "スクエア荏原");
            log.info("検索条件「どこで」を選択");

            // 検索条件「何をする」を選択
            this.webBrowser.selectOptionByXpath("//*[@id=\"purpose\"]", "バスケットボール");
            log.info("検索条件「何をする」を選択");

            // 検索ボタンを押す
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("検索ボタンを押下");

            // 念のため、3秒ロード待機
            this.webBrowser.wait(3);

            log.info("抽選開始時刻まで待機中です...");

            // TODO：指定時刻ぴったりまで待機する

            log.info("早押しを開始します");

            // ログインボタンを押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-login\"]");
            log.info("「ログイン」ボタンを押下");

            // 利用者番号入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"userId\"]", "00004348");
            log.info("「利用者番号」を入力");

            // パスワード入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"password\"]", "Teamreserve0721");
            log.info("「パスワード」を入力");

            // ログインボタン押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("「ログイン」ボタンを押下");

            // 予約枠選択
            this.webBrowser.clickByXpath(this.予約対象.getButtonXpathAfterTwoMonth());
            log.info("枠を選択");

            // 予約ボタン押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("「予約」ボタンを押下");

            // 利用規約に同意
            this.webBrowser.clickByXpath("//*[@id=\"rule\"]/div[3]/label[1]");
            log.info("「利用規約に同意する」ラジオボタンを選択");

            // 確認ボタン押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("「確認」ボタンを押下");

            // 催し物名入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"eventname0\"]", "バスケットボール");
            log.info("「催し物名」を入力");

            // 利用人数入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"peoples0\"]", "20");
            log.info("「利用人数」を入力");

            // 予約ボタン押下
//            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
//            log.info("「予約」ボタンを押下");

            // ドライバを閉じる
            this.webBrowser.quit();
        } catch (Exception e) {
            // ドライバを閉じる
            this.webBrowser.quit();

            log.error("早押し抽選の参加中にエラーが発生したため、予約ができませんでした。");
            e.printStackTrace();

            // エラーをLINEに通知
            LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
            lineMessagingAPI.addMessage("早押し抽選の参加中にエラーが発生したため、予約ができませんでした。エラーメッセージを送ります。");
            lineMessagingAPI.addMessage(e.getMessage());
            lineMessagingAPI.sendSeparate();
            return;
        }

        // 抽選結果結果をLINEに送信
        LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
        lineMessagingAPI.addMessage("【早押し抽選結果】");
        lineMessagingAPI.addMessage(dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "の早押し抽選に参加しました。結果は以下を確認してください。");
        lineMessagingAPI.addMessage("https://www.cm9.eprs.jp/shinagawa/web/");
        lineMessagingAPI.sendAll();
        log.info("LINEに通知を送信");
    }
}
