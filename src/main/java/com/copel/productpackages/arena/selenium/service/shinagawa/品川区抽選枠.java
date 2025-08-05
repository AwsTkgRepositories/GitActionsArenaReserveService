package com.copel.productpackages.arena.selenium.service.shinagawa;

import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;

public enum 品川区抽選枠 {
    午前,
    午後1,
    午後2,
    夜間;

    /**
     * Enumインスタンスを文字列から生成する.
     *
     * @param name Enum名
     * @return Enumインスタンス
     */
    public static 品川区抽選枠 getEnumByName(final String name) {
        for (品川区抽選枠 value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return 品川区抽選枠.午前;
    }

    /**
     * 2ヵ月後同日の枠のボタンのXpathを作成する.
     *
     * @return Xpath
     */
    public String getButtonXpathAfterTwoMonth(final OriginalDate dateAfterTwoMonth) {
        switch (this) {
            case 午前:
                return "//*[@id=\"" + dateAfterTwoMonth.toYYYYMMDD() + "_10\"]/img";
            case 午後1:
                return "//*[@id=\"" + dateAfterTwoMonth.toYYYYMMDD() + "_20\"]/img";
            case 午後2:
                return "//*[@id=\"" + dateAfterTwoMonth.toYYYYMMDD() + "_30\"]/img";
            case 夜間:
                return "//*[@id=\"" + dateAfterTwoMonth.toYYYYMMDD() + "_40\"]/img";
            default:
                return "";
        }
    }

    /**
     * 2ヵ月後同日の枠のInoutのXpathを作成する.
     *
     * @return Xpath
     */
    public String getInputXpathAfterTwoMonth(final OriginalDate dateAfterTwoMonth) {
        switch (this) {
            case 午前:
                return "//*[@id=\"S_" + dateAfterTwoMonth.toYYYYMMDD() + "_10\"]";
            case 午後1:
                return "//*[@id=\"S_" + dateAfterTwoMonth.toYYYYMMDD() + "_20\"]";
            case 午後2:
                return "//*[@id=\"S_" + dateAfterTwoMonth.toYYYYMMDD() + "_30\"]";
            case 夜間:
                return "//*[@id=\"S_" + dateAfterTwoMonth.toYYYYMMDD() + "_40\"]";
            default:
                return "";
        }
    }
}
