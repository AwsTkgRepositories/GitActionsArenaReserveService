package com.copel.productpackages.arena.selenium.service.ota;

import com.copel.productpackages.arena.selenium.service.entity.CourtUsageType;

public enum 大田区体育館 {
    田園調布せせらぎ館,
    大森スポーツセンター;

    public static 大田区体育館 getEnumByName(final String name) {
        for (大田区体育館 value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return 大田区体育館.大森スポーツセンター;
    }

    /**
     * 集会→バスケットボールで検索して表示される次の画面で体育館を選択するためのXpathを返す.
     *
     * @return Xpath
     */
    public String get施設選択画面Xpath() {
        switch (this) {
            case 田園調布せせらぎ館:
                return "//*[@id=\"r_record3\"]";
            case 大森スポーツセンター:
                return "//*[@id=\"r_record4\"]";
            default:
                return null;
        }
    }

    /**
     * 日付ラベル取得用のXpathを返却する.
     *
     * @param dateIndex 日付を動かすためのインデックス
     * @param courtType コート種別
     * @return Xpath
     */
    public String get日付Xpath(final int dateIndex, final CourtUsageType courtType) {
        switch (this) {
            case 田園調布せせらぎ館:
                switch (courtType) {
                    case 全面:
                        // せせらぎ館は全面なし
                        return null;
                    case 半面1:
                        // 体育室A
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[1]/div[2]/table/tbody/tr[1]/th[" + Integer.toString(dateIndex + 1) + "]/p[1]";
                    case 半面2:
                        // 体育室B
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[2]/div[2]/table/tbody/tr[1]/th[" + Integer.toString(dateIndex + 1) + "]/p[1]";
                    default:
                        return null;
                }
            case 大森スポーツセンター:
                switch (courtType) {
                    case 全面:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[1]/div[3]/table/tbody/tr[1]/th[" + Integer.toString(dateIndex + 1) + "]/p[1]";
                    case 半面1:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[2]/div[3]/table/tbody/tr[1]/th[" + Integer.toString(dateIndex + 1) + "]/p[1]";
                    case 半面2:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[3]/div[3]/table/tbody/tr[1]/th[" + Integer.toString(dateIndex + 1) + "]/p[1]";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    /**
     * 予約枠の一覧画面の枠のコマのXpathを返却する.
     *
     * @param dateIndex 日付を動かすためのインデックス
     * @param slotIndex 予約枠時間帯を動かすためのインデックス（午前: 1、午後: 2、夜間: 3）
     * @param courtType コート種別
     * @return Xpath
     */
    public String get予約枠コマXpath(final int dateIndex, final int slotIndex, final CourtUsageType courtType) {
        switch (this) {
            case 田園調布せせらぎ館:
                switch (courtType) {
                    case 全面:
                        // せせらぎ館は全面なし
                        return null;
                    case 半面1:
                        // 体育室A
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[1]/div[2]/table/tbody/tr[" + Integer.toString(slotIndex + 1) + "]/td[" + Integer.toString(dateIndex) + "]/img";
                    case 半面2:
                        // 体育室B
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[2]/div[2]/table/tbody/tr[" + Integer.toString(slotIndex + 1) + "]/td[" + Integer.toString(dateIndex) + "]/img";
                    default:
                        return null;
                }
            case 大森スポーツセンター:
                switch (courtType) {
                    case 全面:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[1]/div[3]/table/tbody/tr[" + Integer.toString(slotIndex + 1) + "]/td[" + Integer.toString(dateIndex) + "]/img";
                    case 半面1:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[2]/div[3]/table/tbody/tr[" + Integer.toString(slotIndex + 1) + "]/td[" + Integer.toString(dateIndex) + "]/img";
                    case 半面2:
                        return "//*[@id=\"formMain\"]/main/div[3]/div[1]/div[3]/ul/li[3]/div[3]/table/tbody/tr[" + Integer.toString(slotIndex + 1) + "]/td[" + Integer.toString(dateIndex) + "]/img";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
