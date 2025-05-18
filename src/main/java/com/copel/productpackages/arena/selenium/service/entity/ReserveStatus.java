package com.copel.productpackages.arena.selenium.service.entity;

public enum ReserveStatus {
    予約可能,
    予約不可,
    その他;

    public static ReserveStatus getEnumByShinagawa(final String status) {
        switch (status) {
            case "受付期間外" :
                return その他;
            case "休館日" :
                return 予約不可;
            case "予約あり" :
                return 予約不可;
            case "空き" :
                return 予約可能;
            default :
                return その他;
        }
    }

    public String getIcon() {
        if (予約可能.equals(this)) {
            return "○";
        } else if (予約不可.equals(this)) {
            return "×";
        } else {
            return "-";
        }
    }
}
