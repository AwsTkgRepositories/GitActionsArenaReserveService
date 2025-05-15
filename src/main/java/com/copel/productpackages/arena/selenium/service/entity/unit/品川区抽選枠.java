package com.copel.productpackages.arena.selenium.service.entity.unit;

public enum 品川区抽選枠 {
    午前,
    午後1,
    午後2,
    夜間;

    /**
     * 2ヵ月後同日の枠のボタンのXpathを作成する.
     *
     * @return Xpath
     */
    public String getButtonXpathAfterTwoMonth() {
        OriginalDate dateAfterTwoMonth = new OriginalDate();
        dateAfterTwoMonth.plusMonths(2);
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
}
