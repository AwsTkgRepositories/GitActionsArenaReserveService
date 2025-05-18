package com.copel.productpackages.arena.selenium.service.shinagawa;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 品川区の早押し抽選処理クラス.
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
     * 環境変数.
     * GitHUB Actionsに入力された対象コート名.
     */
    private static String TARGET_COURT_NAME = System.getenv("TARGET_COURT_NAME");
    /**
     * 環境変数.
     * GitHUB Actionsに入力された対象時間枠名.
     */
    private static String TARGET_TIME_SLOT = System.getenv("TARGET_TIME_SLOT");
    /**
     * 定数.
     * 早押し抽選開始時刻の時.
     */
    private static int START_TIME_HOUR = 10;
    /**
     * 定数.
     * 早押し抽選開始時刻の分.
     */
    private static int START_TIME_MINUTE = 0;

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service
            = new ArenaResereQuickLotteryServiceShinagawa(
                    LINE_CHANNEL_ACCESS_TOKEN, 
                    NOTIFY_LINE_ID,
                    ACCOUNT_ID,
                    ACCOUNT_PASSWORD,
                    品川区体育館.getEnumByName(TARGET_ARENA_NAME),
                    TARGET_COURT_NAME,
                    品川区抽選枠.getEnumByName(TARGET_TIME_SLOT));
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
     * 予約対象のコート名.
     */
    private String targetCourtName;
    /**
     * 予約対象の枠.
     */
    private 品川区抽選枠 予約対象;

    /**
     * コンストラクタ.
     */
    public ArenaResereQuickLotteryServiceShinagawa(final String channelAccessToken, final String toLineId,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this.webBrowser = new WebBrowser(true);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
        this.targetCourtName = targetCourtName;
        this.予約対象 = 予約対象;
    }
    public ArenaResereQuickLotteryServiceShinagawa(final boolean isHeadlessMode, final String channelAccessToken, final String toLineId,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this.webBrowser = new WebBrowser(isHeadlessMode);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
        this.targetCourtName = targetCourtName;
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
            this.webBrowser.selectOptionByXpath("//*[@id=\"bname\"]", this.targetArena.name());
            log.info("検索条件「どこで」を選択");

            // 検索条件「何をする」を選択
            this.webBrowser.selectOptionByXpath("//*[@id=\"purpose\"]", "バスケットボール");
            log.info("検索条件「何をする」を選択");

            // 検索ボタンを押す
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("検索ボタンを押下");

            // 念のため、3秒ロード待機
            this.webBrowser.wait(3);

            // 開始時刻と現在時刻の差分を作成する
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
            ZonedDateTime targetTime = now.withHour(START_TIME_HOUR).withMinute(START_TIME_MINUTE).withSecond(1).withNano(0);
            Duration duration = Duration.between(now, targetTime);

            // 開始時刻以降に実行された場合、処理終了する
            if (now.isAfter(targetTime)) {
                log.info("日本時間" + Integer.toString(START_TIME_HOUR) + ":" + Integer.toString(START_TIME_MINUTE) + "以降に実行されたため、処理を終了します");
                // ドライバを閉じる
                this.webBrowser.quit();
                return;
            }

            // 日本時間で開始時刻まで待機する（バッファを持ち1秒遅れスタートさせる）
            log.info("日本時間" + Integer.toString(START_TIME_HOUR) + ":" + Integer.toString(START_TIME_MINUTE) + "まで " + duration.toSeconds() + " 秒待機します...");
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());

            // 開始時刻になったら処理を再開
            log.info("日本時間" + Integer.toString(START_TIME_HOUR) + ":" + Integer.toString(START_TIME_MINUTE) + "になったため、早押し処理を開始します");

            // ログインボタンを押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-login\"]");
            log.info("「ログイン」ボタンを押下");

            // 利用者番号入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"userId\"]", this.accountId);
            log.info("「利用者番号」で" + this.accountId + "を入力");

            // パスワード入力
            this.webBrowser.sendKeysByXpath("//*[@id=\"password\"]", this.accountPassword);
            log.info("「パスワード」で" + this.accountPassword + "を入力");

            // ログインボタン押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("「ログイン」ボタンを押下");

            // 「館」を選択
            this.webBrowser.selectOptionByXpath("//*[@id=\"mansion-select\"]", this.targetArena.name());
            log.info("「館」で" + this.targetArena.name() + "を選択");

            // 「施設」を選択
            this.webBrowser.selectOptionByXpath("//*[@id=\"facility-select\"]", this.targetArena.getDisplayWithCourtName(this.targetCourtName));
            log.info("「施設」でアリーナ" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "を選択");

            // 予約しようとしている対象の枠が空きではなかった場合、処理終了
            String status = this.webBrowser.getAltAttributeByXpath(this.予約対象.getButtonXpathAfterTwoMonth());
            if (!"空き".equals(status)) {
                log.error(dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "は空いていないため、早押し抽選での予約ができませんでした。");
                // エラーをLINEに通知
                LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
                lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nは空いていないため、予約できませんでした。");
                lineMessagingAPI.sendSeparate();
                return;
            }

            // 予約枠選択
            this.webBrowser.clickByXpath(this.予約対象.getButtonXpathAfterTwoMonth());
            log.info("枠「" + this.予約対象.getButtonXpathAfterTwoMonth() + "」を選択");

            // 予約枠選択時の描画の変化を待機する
            this.webBrowser.waitForInputValueChange(this.予約対象.getInputXpathAfterTwoMonth(), "1");
            log.info("枠「" + this.予約対象.getInputXpathAfterTwoMonth() + "」が選択後の値に変化しました");

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

            // 送信ボタン押下
            this.webBrowser.clickByXpath("//*[@id=\"btn-go\"]");
            log.info("「送信」ボタンを押下");

            // アラートのOKを押下
            this.webBrowser.waitForAlertAndAccept();
            log.info("アラートのOKを押下");

            this.webBrowser.wait(3);
            log.info("早押し抽選処理は正常に終了しました");

            // ドライバを閉じる
            this.webBrowser.quit();
        } catch (Exception e) {
            // ドライバを閉じる
            this.webBrowser.quit();

            log.error("早押し抽選の参加中にエラーが発生したため、予約ができませんでした。");
            e.printStackTrace();

            // エラーをLINEに通知
            LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
            lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの予約中にエラーが発生しました。エラーメッセージは以下です。");
            lineMessagingAPI.addMessage(new ObjectMapper().writeValueAsString(e.getMessage()));
            lineMessagingAPI.sendSeparate();
            return;
        }

        // 抽選結果結果をLINEに送信
        LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
        lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの早押し抽選に参加しました。結果は以下を確認してください。\\n");
        lineMessagingAPI.addMessage("https://www.cm9.eprs.jp/shinagawa/web/");
        lineMessagingAPI.sendAll();
        log.info("LINEに通知を送信");
    }
}
