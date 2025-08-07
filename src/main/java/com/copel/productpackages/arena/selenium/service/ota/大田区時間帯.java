package com.copel.productpackages.arena.selenium.service.ota;

public enum 大田区時間帯 {
    午前,
    午後1,
    午後2,
    夜間;

    /**
     * このenumのindexを各体育館ごとに取得します(1はじまり).
     * 例：せせらぎ館の午後2は"3", 大森スポセンの夜間は"3"など、画面上での上から何番目の行か？を示すインデックス
     *
     * @return インデックス
     */
    public int getIndex(final 大田区体育館 arena) {
        switch (arena) {
            case 田園調布せせらぎ館:
                return this.ordinal() + 1;
            case 大森スポーツセンター:
                switch (this) {
                    case 午前:
                        return 1;
                    case 午後1:
                        return 2;
                    case 午後2:
                        return -1;
                    case 夜間:
                        return 3;
                    default:
                        return 0;
                }
            default:
                return -1;
        }
    }
}
