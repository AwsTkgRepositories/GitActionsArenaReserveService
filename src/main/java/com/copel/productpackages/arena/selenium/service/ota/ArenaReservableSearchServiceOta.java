package com.copel.productpackages.arena.selenium.service.ota;

import java.io.IOException;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlot;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlotLot;
import com.copel.productpackages.arena.selenium.service.entity.ReserveStatus;
import com.copel.productpackages.arena.selenium.service.entity.TimeSlot;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;

import lombok.extern.slf4j.Slf4j;

/**
 * 大田区の体育館空き状況の検索処理クラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class ArenaReservableSearchServiceOta {
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
     * GitHUB Actionsに入力された対象体育館名.
     */
    private static String TARGET_ARENA_NAME = System.getenv("TARGET_ARENA_NAME");
    /**
     * 環境変数.
     * GitHUB Actionsに入力された対象コート名.
     */
    private static String TARGET_COURT_NAME = System.getenv("TARGET_COURT_NAME");

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ArenaReservableSearchServiceOta service
            = new ArenaReservableSearchServiceOta(
                    LINE_CHANNEL_ACCESS_TOKEN,
                    NOTIFY_LINE_ID,
                    大田区体育館.getEnumByName(TARGET_ARENA_NAME),
                    CourtUsageType.getEnumByCourtName大田区(大田区体育館.getEnumByName(TARGET_ARENA_NAME), TARGET_COURT_NAME));
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
     * 予約対象の体育館.
     */
    private 大田区体育館 targetArena;
    /**
     * 確認対象のコート.
     */
    private CourtUsageType courtType;

    /**
     * コンストラクタ.
     */
    public ArenaReservableSearchServiceOta(final String channelAccessToken, final String toLineId, final 大田区体育館 targetArena, final CourtUsageType courtType) {
        this.webBrowser = new WebBrowser(true);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.targetArena = targetArena;
        this.courtType = courtType;
    }
    public ArenaReservableSearchServiceOta(final boolean isHeadless, final String channelAccessToken, final String toLineId, final 大田区体育館 targetArena, final CourtUsageType courtType) {
        this.webBrowser = new WebBrowser(isHeadless);
        this.toLineId = toLineId;
        this.channelAccessToken = channelAccessToken;
        this.targetArena = targetArena;
        this.courtType = courtType;
    }

    /**
     * 処理.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void execute() throws InterruptedException, IOException {
        // うぐいすネットにアクセス
        this.webBrowser.access("https://www.yoyaku.city.ota.tokyo.jp/eshisetsu/menu/Welcome.cgi");

        try {
            // 一覧画面へ遷移
            this.webBrowser.clickByXpath("//*[@id=\"formMain\"]/a");
            log.info("「ログインせずに空き状況を検索」を選択");

            this.webBrowser.clickByXpath("//*[@id=\"jouken1\"]/div[2]/ul/li[1]/label");
            log.info("「利用目的で検索」を選択");

            this.webBrowser.scrollToElementByXpath("//*[@id=\"catSel1\"]/div[2]/ul/li[1]/label");
            this.webBrowser.clickByXpath("//*[@id=\"catSel1\"]/div[2]/ul/li[1]/label");
            log.info("「集会」を選択");

            this.webBrowser.scrollToElementByXpath("//*[@id=\"genSel1\"]/div[2]/ul/li[38]/label");
            this.webBrowser.clickByXpathWithJS("//*[@id=\"genSel1\"]/div[2]/ul/li[38]/label");
            log.info("スクロールして「バスケットボール」を選択");

            this.webBrowser.scrollToElementByXpath("//*[@id=\"submitbutton\"]");
            this.webBrowser.clickByXpathWithJS("//*[@id=\"submitbutton\"]");
            log.info("「選択した条件で次へ」を選択");

            this.webBrowser.clickByXpathWithJS(this.targetArena.get施設選択画面Xpath());
            log.info("「" + this.targetArena.name() + "」を選択");

            this.webBrowser.clickByXpath("//*[@id=\"submitbutton\"]");
            log.info("「選択した施設で検索」を選択");

            // 空き状況を取得
            ReservationSlotLot slotLot = new ReservationSlotLot();
            OriginalDate チェック済日付 = new OriginalDate();
            boolean isContinue = true;
            while (isContinue) {
                for (int i = 1; i <= 7; i++) {
                    String 日付ラベル = this.webBrowser.getTextByXpath(this.targetArena.get日付Xpath(i, this.courtType));
                    OriginalDate 日付 = new OriginalDate(日付ラベル);

                    // 年を跨いだら翌年として更新する
                    if (日付.is1月1日()) {
                        日付.plusYear(1);
                    }

                    String 午前 = this.webBrowser.getAltAttributeByXpath(this.targetArena.get予約枠コマXpath(i, 1, this.courtType));
                    log.info("{}午前: {}", 日付.toDisplayStringWithoutYear(), 午前);
                    ReservationSlot slot = new ReservationSlot();
                    slot.setSlotDate(日付);
                    slot.setUsageType(this.courtType);
                    slot.setReserveStatus(ReserveStatus.getEnumByOta(午前));
                    slot.setTimeSlot(TimeSlot.getTimeSlotOtaByName("午前", 日付));
                    slotLot.add(slot);

                    String 午後 = this.webBrowser.getAltAttributeByXpath(this.targetArena.get予約枠コマXpath(i, 2, this.courtType));
                    log.info("{}午後: {}", 日付.toDisplayStringWithoutYear(), 午後);
                    slot = new ReservationSlot();
                    slot.setSlotDate(日付);
                    slot.setUsageType(this.courtType);
                    slot.setReserveStatus(ReserveStatus.getEnumByOta(午後));
                    slot.setTimeSlot(TimeSlot.getTimeSlotOtaByName("午後", 日付));
                    slotLot.add(slot);

                    String 夜間 = this.webBrowser.getAltAttributeByXpath(this.targetArena.get予約枠コマXpath(i, 3, this.courtType));
                    log.info("{}夜間: {}", 日付.toDisplayStringWithoutYear(), 夜間);
                    slot = new ReservationSlot();
                    slot.setSlotDate(日付);
                    slot.setUsageType(this.courtType);
                    slot.setReserveStatus(ReserveStatus.getEnumByOta(夜間));
                    slot.setTimeSlot(TimeSlot.getTimeSlotOtaByName("夜間", 日付));
                    slotLot.add(slot);

                    // 本日から7ヵ月後ちょうどの日付、または既にチェック済みの日付が今チェックしている日付よりも後の場合(無限ループを阻止)、処理を終了する
                    if (日付.isSameAsTodayPlusMonthsFromToday(7) || チェック済日付.isAfter(日付)) {
                        isContinue = false;
                         break;
                    } else {
                        // チェック済日付を更新する
                        チェック済日付 = 日付;
                    }
                }
                this.webBrowser.clickByXpath("//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[1]/div[1]/a[2]");
                log.info("「次の7日分」をクリック");
            }

            // ドライバを閉じる
            this.webBrowser.quit();

            // 【通知文短縮化対応】
            // 大森スポーツセンターは平日の予定が不要なため、土日祝の予定だけに絞る
            if (大田区体育館.大森スポーツセンター.equals(this.targetArena)) {
                slotLot = slotLot.filter土日祝日Only();
            }

            // 検索結果をLINEに送信
            if (slotLot.isTargetExists()) {
                LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
                lineMessagingAPI.addMessage("【大田区】\\n【" + this.targetArena.name() + "・" + this.courtType.getCourtNameBy大田区体育館(this.targetArena) + "】\\n\\n");
                lineMessagingAPI.addMessage(slotLot.toString());
                lineMessagingAPI.sendAll();
                log.info("LINEに通知を送信しました");
            } else {
                log.info("通知対象の枠が存在しないため、LINE通知を行いませんでした");
            }
        } catch (Exception e) {
            // ドライバを閉じる
            this.webBrowser.quit();

            log.error("大田区の体育館空き状況の検索中にエラーが発生したため、処理を停止しました。");
            e.printStackTrace();

            // エラーをLINEに通知
            LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, this.toLineId);
            lineMessagingAPI.addMessage("【大田区】\\n【空き状況取得】\\n体育館空き状況の検索中にエラーが発生したため、処理を停止しました。");
            lineMessagingAPI.addMessage(e.getMessage());
            lineMessagingAPI.sendSeparate();
        }
    }
}
