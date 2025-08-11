package com.copel.productpackages.arena.selenium.service.setagaya;

public enum 世田谷区体育館 {
    宮坂区民センター;

    public static 世田谷区体育館 getEnumByName(final String name) {
        for (世田谷区体育館 value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return 世田谷区体育館.宮坂区民センター;
    }


    /**
     * 集会→バスケットボールで検索して表示される次の画面で体育館を選択するためのXpathを返す.
     *
     * @return Xpath
     */
    public String get施設選択画面Xpath() {
        switch (this) {
            case 宮坂区民センター:
                return "//*[@id=\"shisetsutbl\"]/tr[1]/td[2]/label";
            default:
                return null;
        }
    }
}
