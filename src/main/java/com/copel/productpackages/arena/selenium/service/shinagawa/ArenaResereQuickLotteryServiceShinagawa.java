package com.copel.productpackages.arena.selenium.service.shinagawa;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.copel.productpackages.arena.selenium.service.entity.ReserveStatus;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDateTime;
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
    private static List<String> NOTIFY_LINE_ID_LIST = System.getenv("NOTIFY_LINE_ID_LIST") != null ? Arrays.asList(System.getenv("NOTIFY_LINE_ID_LIST").split(",")) : List.of();
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
     * 環境変数.
     * GitHUB Actionsに入力された対象日付..
     */
    private static String TARGET_DATE = System.getenv("TARGET_DATE");
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
     * 品川区施設予約システムのURL.
     */
    private static final String SHINAGAWA_RESERVE_SYSTEM_URL = "https://www.cm9.eprs.jp/shinagawa/web/";

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ArenaResereQuickLotteryServiceShinagawa service = null;
        if (TARGET_DATE != null && !TARGET_DATE.isEmpty()) {
            try {
                OriginalDate targetDate = new OriginalDate(TARGET_DATE);
                service = new ArenaResereQuickLotteryServiceShinagawa(
                        targetDate,
                        true,
                        LINE_CHANNEL_ACCESS_TOKEN, 
                        NOTIFY_LINE_ID_LIST,
                        ACCOUNT_ID,
                        ACCOUNT_PASSWORD,
                        品川区体育館.getEnumByName(TARGET_ARENA_NAME),
                        TARGET_COURT_NAME,
                        品川区抽選枠.getEnumByName(TARGET_TIME_SLOT));
            } catch (IllegalArgumentException e) {
                log.error("入力された対象日付が不正です。処理を終了します。 入力された値: {}", TARGET_DATE);
            }
        } else {
            service = new ArenaResereQuickLotteryServiceShinagawa(
                    LINE_CHANNEL_ACCESS_TOKEN, 
                    NOTIFY_LINE_ID_LIST,
                    ACCOUNT_ID,
                    ACCOUNT_PASSWORD,
                    品川区体育館.getEnumByName(TARGET_ARENA_NAME),
                    TARGET_COURT_NAME,
                    品川区抽選枠.getEnumByName(TARGET_TIME_SLOT));
        }
        service.execute();
    }

    /**
     * WebBrowserクライアント.
     */
    private WebBrowser webBrowser;
    /**
     * 結果の送信先LINE ID.
     */
    private List<String> notifyLineIdList;
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
     * 予約対象日付.
     * 通常は2ヵ月後同日を生成して予約を実行するが、ローカル実行など特定の日付を指定して予約を行いたい場合に対応
     */
    private OriginalDate targetDate = null;

    /**
     * コンストラクタ.
     */
    public ArenaResereQuickLotteryServiceShinagawa(final String channelAccessToken, final List<String> notifyLineIdList,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this(true, channelAccessToken, notifyLineIdList,accountId, accountPassword, targetArena, targetCourtName, 予約対象);
    }
    public ArenaResereQuickLotteryServiceShinagawa(final boolean isHeadlessMode, final String channelAccessToken, final List<String> notifyLineIdList,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this.webBrowser = new WebBrowser(isHeadlessMode);
        this.notifyLineIdList = notifyLineIdList;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
        this.targetCourtName = targetCourtName;
        this.予約対象 = 予約対象;
    }
    public ArenaResereQuickLotteryServiceShinagawa(final OriginalDate targetDate, final int processStartTimeHour, final int processStartTimeMinutes, final boolean isHeadlessMode, final String channelAccessToken, final List<String> notifyLineIdList,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this(isHeadlessMode, channelAccessToken, notifyLineIdList,accountId, accountPassword, targetArena, targetCourtName, 予約対象);
        this.targetDate = targetDate;
        START_TIME_HOUR = processStartTimeHour;
        START_TIME_MINUTE = processStartTimeMinutes;
    }
    public ArenaResereQuickLotteryServiceShinagawa(final OriginalDate targetDate, final boolean isHeadlessMode, final String channelAccessToken, final List<String> notifyLineIdList,final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName, final 品川区抽選枠 予約対象) {
        this(isHeadlessMode, channelAccessToken, notifyLineIdList,accountId, accountPassword, targetArena, targetCourtName, 予約対象);
        this.targetDate = targetDate;
        OriginalDateTime targetTime = new OriginalDateTime();
        targetTime.plusSeconds(60);
        START_TIME_HOUR = targetTime.getHour();
        START_TIME_MINUTE = targetTime.getMinute();
    }

    /**
     * 処理.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void execute() throws InterruptedException, IOException {
        // 2ヵ月後同日の日付を作成
        OriginalDate dateAfterTwoMonth = new OriginalDate();
        dateAfterTwoMonth.plusMonths(2);

        // 予約対象日付が設定されている場合、dateAfterTwoMonthをthis.targetDateで置き換える
        if (this.targetDate != null) {
            dateAfterTwoMonth = this.targetDate;
        }

        // 開始ログを出力
        log.info("品川区の早押し抽選処理を開始します. 日付: {}, 体育館: {}, コート:{}, 枠: {}",
                dateAfterTwoMonth, this.targetArena, this.targetCourtName, this.予約対象);
 
        // (0) URL先へアクセスする
        this.webBrowser.access(SHINAGAWA_RESERVE_SYSTEM_URL);

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

            String buttonXpathAfterTwoMonth = this.予約対象.getButtonXpathAfterTwoMonth(dateAfterTwoMonth);
            ReserveStatus status = ReserveStatus.getEnumByShinagawa(this.webBrowser.getAltAttributeByXpath(buttonXpathAfterTwoMonth));

            // 予約しようとしている対象の枠が空きではなかった場合、処理終了
            if (!status.is現在予約可能()) {
                log.error(dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "は空いていないため、早押し抽選での予約ができませんでした。 現在の枠の状態: {}", status);
                log.debug("↑を検索した際のxpath:{}, status: {}", buttonXpathAfterTwoMonth, status);
                // エラーをLINEに通知
                for (final String notifyLineId : this.notifyLineIdList) {
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nは空いていないため、予約できませんでした。");
                    lineMessagingAPI.sendSeparate();
                }
                return;
            } else {
                log.info(dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "は予約可能です。処理を続行します。");
            }

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

            // 日本時間で開始時刻まで待機する
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

            // 予約枠選択
            this.webBrowser.clickByXpath(buttonXpathAfterTwoMonth);
            log.info("枠「" + buttonXpathAfterTwoMonth + "」を選択");

            // 予約枠選択時の描画の変化を待機する
            String inputXpathAfterTwoMonth = this.予約対象.getInputXpathAfterTwoMonth(dateAfterTwoMonth);
            this.webBrowser.waitForInputValueChange(inputXpathAfterTwoMonth, "1");
            log.info("枠「" + inputXpathAfterTwoMonth + "」が選択後の値に変化しました");

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
            log.info("アラート「OK」を押下");

            // さらにアラートが表示されたらOKを押下
            if (this.webBrowser.isAlertPresent()) {
                this.webBrowser.waitForAlertAndAccept();
                log.info("アラート「OK」を押下");
            }

            this.webBrowser.wait(5);

            try {
                String 画面タイトル = this.webBrowser.getTextByXpath("//*[@id=\"fin-reservation-info\"]/h2");
                // 予約完了画面に遷移している場合
                if ("予約完了".equals(画面タイトル)) {
                    log.info("早押し抽選に参加し、予約に成功しました！");

                    String 予約番号 = "";
                    String 時間 = "";
                    String 利用料金 = "";
                    try {
                        予約番号 = this.webBrowser.getTextByXpath("//*[@id=\"fin-reservation-info\"]/table/tbody/tr[1]/td");
                        時間 = this.webBrowser.getTextByXpath("//*[@id=\"fin-reservation-info\"]/table/tbody/tr[2]/td");
                        利用料金 = this.webBrowser.getTextByXpath("//*[@id=\"fin-reservation-info\"]/table/tbody/tr[7]/td");
                    } catch (Exception e) {
                        // 何もしない
                    }
                    log.info("予約画面の表示内容: 予約番号:{}, 時間: {}, 利用料金: {}", 予約番号, 時間, 利用料金);

                    // 抽選結果結果をLINEに送信
                    for (final String notifyLineId : this.notifyLineIdList) {
                        LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                        lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + this.targetArena.name() + "\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() +this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの早押し抽選に参加し予約が完了しました。結果は以下を確認してください。\\n");
                        lineMessagingAPI.addMessage(SHINAGAWA_RESERVE_SYSTEM_URL);
                        lineMessagingAPI.sendAll();
                    }
                    log.info("LINEに通知を送信しました");

                } else {
                    log.info("早押し抽選に参加しましたが予約を勝ち取る事ができませんでした");

                    // 抽選結果結果をLINEに送信
                    for (final String notifyLineId : this.notifyLineIdList) {
                        LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                        lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + this.targetArena.name() + "\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() +this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの早押し抽選に参加しましたが予約を取れませんでした。\\n");
                        lineMessagingAPI.addMessage(SHINAGAWA_RESERVE_SYSTEM_URL);
                        lineMessagingAPI.sendAll();
                    }
                    log.info("LINEに通知を送信しました");
                }
            } catch (Exception e) {
                log.info("早押し抽選に参加しましたが予約を勝ち取る事ができませんでした");

                // 抽選結果結果をLINEに送信
                for (final String notifyLineId : this.notifyLineIdList) {
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + this.targetArena.name() + "\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() +this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの早押し抽選に参加しましたが予約を取れませんでした。\\n");
                    lineMessagingAPI.addMessage(SHINAGAWA_RESERVE_SYSTEM_URL);
                    lineMessagingAPI.sendAll();
                }
                log.info("LINEに通知を送信しました");
            }
        } catch (Exception e) {
            log.error("早押し抽選の参加中にエラーが発生したため、予約ができませんでした。");
            e.printStackTrace();

            // エラーをLINEに通知
            for (final String notifyLineId : this.notifyLineIdList) {
                LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                lineMessagingAPI.addMessage("【品川区】\\n【早押し抽選結果】\\n\\n" + this.targetArena.name() + "\\n" + dateAfterTwoMonth.toDisplayStringWithoutYear() + this.予約対象.name() + "\\n" + this.targetArena.getDisplayWithCourtName(this.targetCourtName) + "\\nの予約中にエラーが発生しました。エラーメッセージは以下です。");
                lineMessagingAPI.addMessage(new ObjectMapper().writeValueAsString(e.getMessage()));
                lineMessagingAPI.sendSeparate();
            }
            log.info("LINEに通知を送信しました");
            return;
        } finally {
            // ドライバを閉じる
            this.webBrowser.quit();
        }
    }
}
