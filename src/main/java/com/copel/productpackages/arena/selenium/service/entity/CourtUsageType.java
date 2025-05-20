package com.copel.productpackages.arena.selenium.service.entity;

import com.copel.productpackages.arena.selenium.service.ota.大田区体育館;

/**
 * コートの大きさ
 *
 * @author 鈴木一矢
 *
 */
public enum CourtUsageType {
    全面,
    半面1,
    半面2;

    /**
     * このEnumを体育館とコート名から取得する.
     *
     * @param arena 体育館
     * @param courtName コート名
     * @return Enumオブジェクト
     */
    public static CourtUsageType getEnumByCourtName大田区(final 大田区体育館 arena, final String courtName) {
        switch (arena) {
            case 田園調布せせらぎ館:
                if ("体育室A".equals(courtName)) {
                    return 半面1;
                } else if ("体育室B".equals(courtName)) {
                    return 半面2;
                } else {
                    return 全面;
                }
            case 大森スポーツセンター:
                if ("主競技場".equals(courtName)) {
                    return 全面;
                } else if ("主競技場北".equals(courtName)) {
                    return 半面1;
                } else if ("主競技場南".equals(courtName)) {
                    return 半面2;
                } else {
                    return 全面;
                }
            default:
                return 全面;
        }
    }

    /**
     * 大田区の体育館のコート名を取得する.
     *
     * @param arena 大田区の体育館
     * @return コート名
     */
    public String getCourtNameBy大田区体育館(final 大田区体育館 arena) {
        switch (arena) {
            case 田園調布せせらぎ館:
                switch (this) {
                    case 全面:
                        // せせらぎ館は全面なし
                        return null;
                    case 半面1:
                        return "体育室A";
                    case 半面2:
                        return "体育室B";
                    default:
                        return null;
                }
            case 大森スポーツセンター:
                switch (this) {
                    case 全面:
                        return "主競技場";
                    case 半面1:
                        return "主競技場北";
                    case 半面2:
                        return "主競技場南";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
