package com.copel.productpackages.arena.selenium.service.shinagawa;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.TimeoutException;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlot;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlotLot;
import com.copel.productpackages.arena.selenium.service.entity.ReserveStatus;
import com.copel.productpackages.arena.selenium.service.entity.TimeSlot;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDateTime;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;

import lombok.extern.slf4j.Slf4j;

/**
 * 品川区の体育館空き状況の検索処理クラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class ArenaReservableSearchServiceShinagawa {
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
     * GitHUB Actionsに入力された対象体育館名.
     */
    private static String TARGET_ARENA_NAME = System.getenv("TARGET_ARENA_NAME");

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ArenaReservableSearchServiceShinagawa service
            = new ArenaReservableSearchServiceShinagawa(
                    LINE_CHANNEL_ACCESS_TOKEN,
                    NOTIFY_LINE_ID_LIST,
                    品川区体育館.getEnumByName(TARGET_ARENA_NAME));
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
     * 予約対象の体育館.
     */
    private 品川区体育館 targetArena;

    /**
     * コンストラクタ.
     */
    public ArenaReservableSearchServiceShinagawa(final String channelAccessToken, final List<String> notifyLineIdList, final 品川区体育館 targetArena) {
        this.webBrowser = new WebBrowser(true);
        this.notifyLineIdList = notifyLineIdList;
        this.channelAccessToken = channelAccessToken;
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

        try {
            // (1) 検索条件画面
            // 検索条件「いつ」を選択
            this.webBrowser.clickByXpath("//*[@id=\"when-check-area\"]/label[4]");
            log.info("検索条件「いつ」を選択");
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

            // (2) 検索結果画面
            ReservationSlotLot resultLot = new ReservationSlotLot();
            // 空き状況を取得する
            OriginalDateTime date = new OriginalDateTime();

            ReservationSlot slot = new ReservationSlot();
            while (true) {
                try {
                    // 午前の枠を取得
                    String 午前ステータス = this.webBrowser.getAltAttributeByXpath("//*[@id=\"" + date.getYyyyMMddWithoutSlash() + "_10\"]/img");
                    slot.setSlotDate(date.toOriginalDate());
                    slot.setUsageType(CourtUsageType.全面);
                    OriginalDateTime startTime = new OriginalDateTime(date);
                    startTime.setHour(9);
                    startTime.setMinute(0);
                    OriginalDateTime endTime = new OriginalDateTime(date);
                    endTime.setHour(11);
                    endTime.setMinute(30);
                    TimeSlot timeSlot = new TimeSlot("午前", startTime, endTime);
                    slot.setTimeSlot(timeSlot);
                    slot.setReserveStatus(ReserveStatus.getEnumByShinagawa(午前ステータス));
                    resultLot.add(slot);
                    log.info(slot.toString());

                    if (!slot.is予約閲覧対象()) {
                        log.info("予約可能な全ての日付をチェックしました");
                        this.webBrowser.quit();
                        break;
                    } else {
                        // 午後1の枠を取得
                        String 午後1ステータス = this.webBrowser.getAltAttributeByXpath("//*[@id=\"" + date.getYyyyMMddWithoutSlash() + "_20\"]/img");
                        slot = new ReservationSlot();
                        slot.setSlotDate(date.toOriginalDate());
                        slot.setUsageType(CourtUsageType.全面);
                        startTime = new OriginalDateTime(date);
                        startTime.setHour(12);
                        startTime.setMinute(0);
                        endTime = new OriginalDateTime(date);
                        endTime.setHour(14);
                        endTime.setMinute(30);
                        timeSlot = new TimeSlot("午後1", startTime, endTime);
                        slot.setTimeSlot(timeSlot);
                        slot.setReserveStatus(ReserveStatus.getEnumByShinagawa(午後1ステータス));
                        resultLot.add(slot);
                        log.info(slot.toString());

                        // 午後2の枠を取得
                        String 午後2ステータス = this.webBrowser.getAltAttributeByXpath("//*[@id=\"" + date.getYyyyMMddWithoutSlash() + "_30\"]/img");
                        slot = new ReservationSlot();
                        slot.setSlotDate(date.toOriginalDate());
                        slot.setUsageType(CourtUsageType.全面);
                        startTime = new OriginalDateTime(date);
                        startTime.setHour(15);
                        startTime.setMinute(0);
                        endTime = new OriginalDateTime(date);
                        endTime.setHour(17);
                        endTime.setMinute(30);
                        timeSlot = new TimeSlot("午後2", startTime, endTime);
                        slot.setTimeSlot(timeSlot);
                        slot.setReserveStatus(ReserveStatus.getEnumByShinagawa(午後2ステータス));
                        resultLot.add(slot);
                        log.info(slot.toString());

                        // 夜間の枠を取得
                        String 夜間ステータス = this.webBrowser.getAltAttributeByXpath("//*[@id=\"" + date.getYyyyMMddWithoutSlash() + "_40\"]/img");
                        slot = new ReservationSlot();
                        slot.setSlotDate(date.toOriginalDate());
                        slot.setUsageType(CourtUsageType.全面);
                        startTime = new OriginalDateTime(date);
                        startTime.setHour(18);
                        startTime.setMinute(0);
                        endTime = new OriginalDateTime(date);
                        endTime.setHour(21);
                        endTime.setMinute(0);
                        timeSlot = new TimeSlot("夜間", startTime, endTime);
                        slot.setTimeSlot(timeSlot);
                        slot.setReserveStatus(ReserveStatus.getEnumByShinagawa(夜間ステータス));
                        resultLot.add(slot);
                        log.info(slot.toString());

                        date.plusDays(1);
                    }
                } catch (TimeoutException e) {
                    // 「翌週」ボタンを押す
                    this.webBrowser.clickByXpath("//*[@id=\"next-week\"]");
                    log.info("「翌週」ボタンを押下");
                }
            }

            // 検索結果をLINEに送信
            if (resultLot.isTargetExists()) {
                for (final String notifyLineId : this.notifyLineIdList) {
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【品川区】\\n【" + this.targetArena.name() + "】\\n\\n");
                    lineMessagingAPI.addMessage(resultLot.toString());
                    lineMessagingAPI.sendAll();
                }
                log.info("LINEに通知を送信しました");
            } else {
                log.info("通知対象の枠が存在しないため、LINE通知を行いませんでした");
            }
        } catch (Exception e) {
            // ドライバを閉じる
            this.webBrowser.quit();

            log.error("品川区の体育館空き状況の検索中にエラーが発生したため、処理を停止しました。");
            e.printStackTrace();

            // エラーをLINEに通知
            for (final String notifyLineId : this.notifyLineIdList) {
                LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                lineMessagingAPI.addMessage("【品川区】\\n【空き状況取得】\\n体育館空き状況の検索中にエラーが発生したため、処理を停止しました。");
                lineMessagingAPI.addMessage(e.getMessage());
                lineMessagingAPI.sendSeparate();
            }
        }
    }
}
