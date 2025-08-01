package com.copel.productpackages.arena.selenium.service.util;

public class OriginalStringUtils {

    /**
     * 引数の文字列がNULLや空文字であるかどうかを判定する.
     *
     * @param targetString 判定対象の文字列
     * @return 空であればtrue、それ以外はfalse
     */
    public static boolean isEmpty(final String targetString) {
        return targetString == null || targetString.isEmpty();
    }
}
