package com.copel.productpackages.arena.selenium.service.entity.unit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class OriginalDate {
    /**
     * LodalDateインスタンス.
     */
    private LocalDate date;

    // 対応する日付フォーマットの一覧
    private static final List<DateTimeFormatter> SUPPORTED_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("MM/dd"),
        DateTimeFormatter.ofPattern("MM-dd"),
        DateTimeFormatter.ofPattern("MM.dd"),
        DateTimeFormatter.ofPattern("MMdd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    public OriginalDate(LocalDate date) {
        this.date = date;
    }
    public OriginalDate(String dateStr) {
        for (DateTimeFormatter formatter : SUPPORTED_FORMATTERS) {
            try {
                this.date = LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 無視して次のパターンを試す
            }
        }
        throw new IllegalArgumentException("対応していない日付形式です: " + dateStr);
    }


    public boolean is土日() {
        DayOfWeek dayOfWeek = this.date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * 表示用文字列を返却する（例: "2025年5月5日（月）"）
     *
     * @return 表示用文字列
     */
    public String toDisplayString() {
        return date.getYear() + "年" + date.getMonthValue() + "月" + date.getDayOfMonth() + "日(" + getJapaneseDayOfWeek() + ")";
    }
    public String toDisplayStringWithoutYear() {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "(" + getJapaneseDayOfWeek() + ")";
    }

    private String getJapaneseDayOfWeek() {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "月";
            case TUESDAY: return "火";
            case WEDNESDAY: return "水";
            case THURSDAY: return "木";
            case FRIDAY: return "金";
            case SATURDAY: return "土";
            case SUNDAY: return "日";
            default: return "";
        }
    }
}
