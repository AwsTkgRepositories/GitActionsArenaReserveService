package com.copel.productpackages.arena.selenium.service.setagaya;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.TimeoutException;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlot;
import com.copel.productpackages.arena.selenium.service.entity.ReservationSlotLot;
import com.copel.productpackages.arena.selenium.service.entity.ReserveStatus;
import com.copel.productpackages.arena.selenium.service.entity.TimeSlot;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;
import com.copel.productpackages.arena.selenium.service.util.OriginalStringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 世田谷区の体育館空き状況の検索処理クラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class ArenaReservableSearchServiceSetagaya {
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
     * 追加の通知の宛先LINE ID.
     */
    private static List<String> EXTEND_NOTIFY_LINE_ID_LIST = System.getenv("EXTEND_NOTIFY_LINE_ID_LIST") != null ? Arrays.asList(System.getenv("EXTEND_NOTIFY_LINE_ID_LIST").split(",")) : List.of();
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
     * けやきネットのURL.
     */
    private static String けやきネットURL = "https://setagaya.keyakinet.net/Web/Home/WgR_ModeSelect";

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        List<String> notifyLineIdList = new ArrayList<String>();
        notifyLineIdList.addAll(NOTIFY_LINE_ID_LIST);
        notifyLineIdList.addAll(EXTEND_NOTIFY_LINE_ID_LIST);
        ArenaReservableSearchServiceSetagaya service
            = new ArenaReservableSearchServiceSetagaya(
                    LINE_CHANNEL_ACCESS_TOKEN,
                    notifyLineIdList,
                    世田谷区体育館.getEnumByName(TARGET_ARENA_NAME));
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
    private 世田谷区体育館 targetArena;

    /**
     * コンストラクタ.
     */
    public ArenaReservableSearchServiceSetagaya(final String channelAccessToken, final List<String> notifyLineIdList, final 世田谷区体育館 targetArena) {
        this.webBrowser = new WebBrowser(true);
        this.notifyLineIdList = notifyLineIdList;
        this.channelAccessToken = channelAccessToken;
        this.targetArena = targetArena;
    }
    public ArenaReservableSearchServiceSetagaya(final boolean isHeadless, final String channelAccessToken, final List<String> notifyLineIdList, final 世田谷区体育館 targetArena) {
        this.webBrowser = new WebBrowser(isHeadless);
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
        ReservationSlotLot resultLot = new ReservationSlotLot();

        // けやきネットにアクセス
        this.webBrowser.access(けやきネットURL);

        try {
            // 一覧画面へ遷移
            this.webBrowser.clickByXpath("//*[@id=\"tabs\"]/ul/li[2]/a");
            log.info("「使用目的から探す」を選択");

            this.webBrowser.clickByXpath("//*[@id=\"tabs\"]/div[2]/div[1]/ul/li[3]/label");
            log.info("「屋内スポーツ」を選択");

            this.webBrowser.wait(2);
            this.webBrowser.clickByXpath("//*[@id=\"mokutekiForm\"]/ul/li[2]/label");
            log.info("「バスケットボール(大人)」を選択");

            this.webBrowser.clickByXpath("//*[@id=\"btnSearchViaPurpose\"]");
            log.info("「検索ボタン」を押下");

            this.webBrowser.wait(1);
            this.webBrowser.clickByXpath(this.targetArena.get施設選択画面Xpath());
            log.info("「宮坂区民センター」を選択");

            this.webBrowser.clickByXpath("//*[@id=\"btnNext\"]");
            log.info("「次へ進む」を押下");

            this.webBrowser.wait(2);
            this.webBrowser.clickByXpath("//*[@id=\"lblPeriod1month\"]");
            log.info("「1ヶ月ボタン」を押下");

            for (final String 時間帯 : Arrays.asList("午前", "午後", "夜間")) {

                this.webBrowser.inputDateByXpath("//*[@id=\"dpStartDate\"]", new OriginalDate().toYYYYMMDDwithHyphen());
                log.info("「表示開始日」に本日日付を入力");

                this.webBrowser.clickByXpath("//*[@id=\"btnShowDetail\"]");
                log.info("「その他の条件で絞り込む」を押下");

                if ("午前".equals(時間帯)) {
                    this.webBrowser.clickByXpath("//*[@id=\"body\"]/div[2]/div[1]/div[2]/dl[2]/dd/ul/li[1]/label"); // 午前
                } else if ("午後".equals(時間帯)) {
                    this.webBrowser.clickByXpath("//*[@id=\"body\"]/div[2]/div[1]/div[2]/dl[2]/dd/ul/li[2]/label"); // 午後
                } else if ("夜間".equals(時間帯)) {
                    this.webBrowser.clickByXpath("//*[@id=\"body\"]/div[2]/div[1]/div[2]/dl[2]/dd/ul/li[3]/label"); // 夜間
                }
                log.info("「" + 時間帯 + "」を押下");

                this.webBrowser.clickByXpath("//*[@id=\"btnHyoji\"]");
                log.info("「表示ボタン」を押下");

                this.webBrowser.wait(2);

                // 空き状況を取得
                OriginalDate date = new OriginalDate();
                boolean isContinue = true;
                int i = 0;
                while (isContinue) {
                    try {
                        ReservationSlot reservationSlot = new ReservationSlot();

                        final String 空き状況 = this.webBrowser.getTextByXpath("//*[@id=\"body\"]/div[2]/div[3]/div/div[3]/table/tbody/tr/td[" + Integer.toString(i + 2) + "]/label");

                        // "△"の枠は詳細画面へ遷移して情報を取得
                        if ("△".equals(空き状況)) {
                            this.webBrowser.clickByXpath("//*[@id=\"body\"]/div[2]/div[3]/div/div[3]/table/tbody/tr/td[" + Integer.toString(i + 2) + "]/label");
                            log.info("「{},{}」の{}を押下", date.toDisplayStringWithoutYear(), 時間帯, 空き状況);

                            this.webBrowser.clickByXpath("//*[@id=\"wrap\"]/div[3]/ul/li[1]/a");
                            log.info("「次へ進む」を押下");

                            int index = 0;
                            boolean isEnd = false;
                            while (!isEnd) {
                                try {
                                    String 時間の文字 = this.webBrowser.getTextByXpath("//*[@id=\"body\"]/div[2]/div[2]/div/div[2]/table/thead/tr/th[" + Integer.toString(index + 2) + "]");
                                    String アイコン = this.webBrowser.getTextByXpath("//*[@id=\"body\"]/div[2]/div[2]/div/div[2]/table/tbody/tr/td[" + Integer.toString(index + 2) + "]/label");
                                    if (OriginalStringUtils.isEmpty(アイコン)) {
                                        boolean exist = this.webBrowser.existsByXpath("//*[@id=\"body\"]/div[2]/div[2]/div/div[2]/table/tbody/tr/td[" + Integer.toString(index + 2) + "]/label/i");
                                        if (exist) {
                                            reservationSlot.setReserveStatus(ReserveStatus.getEnumBySetagaya(空き状況));
                                            reservationSlot.setSlotDate(date);
                                            // TODO：午後の場合は午後1,2,3を分ける
                                            reservationSlot.setTimeSlot(TimeSlot.getTimeSlotSetagayaByName(時間帯, date));
                                            reservationSlot.setUsageType(CourtUsageType.全面);
                                            resultLot.add(reservationSlot);
                                            log.info(reservationSlot.toString());
                                        }
                                    } else {
                                        reservationSlot.setReserveStatus(ReserveStatus.getEnumBySetagaya(アイコン));
                                        reservationSlot.setSlotDate(date);
                                        reservationSlot.setTimeSlot(TimeSlot.getTimeSlotSetagayaByName(時間帯, date));
                                        reservationSlot.setUsageType(CourtUsageType.全面);
                                        resultLot.add(reservationSlot);
                                        log.info(reservationSlot.toString());
                                    }
                                } catch (TimeoutException e) {
                                    isEnd = true;
                                }
                                index++;
                            }

                            this.webBrowser.clickByXpath("//*[@id=\"wrap\"]/div[3]/ul/li[2]/a");
                            log.info("「前に戻る」を押下");
                        }
                        //  "△"以外の枠は情報を結果Lotにセット
                        else {
                            reservationSlot.setReserveStatus(ReserveStatus.getEnumBySetagaya(空き状況));
                            reservationSlot.setSlotDate(date);
                            reservationSlot.setTimeSlot(TimeSlot.getTimeSlotSetagayaByName(時間帯, date));
                            reservationSlot.setUsageType(CourtUsageType.全面);
                            resultLot.add(reservationSlot);
                            log.info(reservationSlot.toString());
                        }

                        // 予約閲覧が終了の場合はこれ以上ページ遷移せず処理を終了する
                        if (!reservationSlot.is予約閲覧対象() && i > 0) {
                            isContinue = false;
                        }

                        // インクリメント
                        date.plusDay(1);
                        i++;
                    } catch (TimeoutException e) {
                        i = 0;
                        this.webBrowser.clickByXpath("//*[@id=\"body\"]/div[2]/div[3]/div/div[3]/table/thead/tr/th[1]/ul/li[3]/a");
                        log.info("「翌月ボタン」を押下");
                    } catch (Exception e) {
                        log.error("【世田谷区】空き状況の取得中にエラーが発生しました。" + 時間帯 + "の空き状況取得を中止します。");
                        e.printStackTrace();
                        break;
                    }
                }
            }

            // ドライバを閉じる
            this.webBrowser.quit();

            // 検索結果とエラーをLINEに送信
            for (final String notifyLineId : this.notifyLineIdList) {
                if (resultLot.isTargetExists()) {
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【世田谷区】\\n【" + this.targetArena.name() + "】\\n\\n");
                    lineMessagingAPI.addMessage(resultLot.toString());
                    lineMessagingAPI.sendAll();
                    log.info("LINEに通知を送信しました");
                } else {
                    log.info("通知対象の枠が存在しませんでした");
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【世田谷区】\\n【" + this.targetArena.name() + "】\\n\\n現在、平日夜間または土日祝日の全日で空きがありません");
                    lineMessagingAPI.addMessage(resultLot.toString());
                    lineMessagingAPI.sendAll();
                }
            }
        } catch (Exception e) {
            // ドライバを閉じる
            this.webBrowser.quit();

            log.error("世田谷区の体育館空き状況の検索中にエラーが発生したため、処理を停止しました。");
            e.printStackTrace();

            // 検索結果とエラーをLINEに送信
            for (final String notifyLineId : this.notifyLineIdList) {
                if (resultLot.isTargetExists()) {
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【世田谷区】\\n【" + this.targetArena.name() + "】\\n\\n空き状況の取得中にエラーが発生しました。途中まで取得できた分を通知します。\\n\\n");
                    lineMessagingAPI.addMessage(resultLot.toString());
                    lineMessagingAPI.sendAll();
                    log.info("LINEに通知を送信しました");
                } else {
                    log.info("通知対象の枠が存在しませんでした");
                    LineMessagingAPI lineMessagingAPI = new LineMessagingAPI(this.channelAccessToken, notifyLineId);
                    lineMessagingAPI.addMessage("【世田谷区】\\n【" + this.targetArena.name() + "】\\n\\n世田谷区の空き状況の取得中にエラーが発生しました。");
                    lineMessagingAPI.addMessage(resultLot.toString());
                    lineMessagingAPI.sendAll();
                }
            }
}
    }
}
