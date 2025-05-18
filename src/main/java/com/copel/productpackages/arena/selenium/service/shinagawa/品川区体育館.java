package com.copel.productpackages.arena.selenium.service.shinagawa;

public enum 品川区体育館 {
    総合体育館,
    戸越体育館,
    スクエア荏原;

    public static 品川区体育館 getEnumByName(final String name) {
        for (品川区体育館 value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return 品川区体育館.スクエア荏原;
    }

    /**
     * コート名を含めた表示名を取得する.
     *
     * @param courtName コート名
     * @return 表示名
     */
    public String getDisplayWithCourtName(final String courtName) {
        switch (this) {
            case 総合体育館:
            case 戸越体育館:
                return "主競技場" + courtName;
            case スクエア荏原:
                return "アリーナ" + courtName;
            default :
                return "アリーナ" + courtName;
        }
    }
}
