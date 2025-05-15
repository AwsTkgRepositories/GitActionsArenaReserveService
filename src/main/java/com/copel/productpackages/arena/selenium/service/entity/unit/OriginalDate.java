package com.copel.productpackages.arena.selenium.service.entity.unit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OriginalDate {
    /**
     * 祝日取得APIエンドポイント.
     */
    private static final String HOLIDAY_API_TEMPLATE = "https://date.nager.at/api/v3/PublicHolidays/%d/JP";
    /**
     * 祝日の一覧.
     */
    private static final Set<LocalDate> holidays;

    /**
     * クラスロード時に祝日取得APIを実行し一覧を取得する.
     */
    static {
        holidays = new HashSet<>();
        OriginalDateTime today = new OriginalDateTime();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(HOLIDAY_API_TEMPLATE, today.getYear())))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("祝日APIの取得に失敗しました。HTTPステータス: " + response.statusCode());
            }

            String body = response.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);

            for (JsonNode node : root) {
                String dateStr = node.get("date").asText(); // yyyy-MM-dd
                LocalDate date = LocalDate.parse(dateStr);
                holidays.add(date);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // 来年の祝日を取得
        today.plusDays(365);
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(HOLIDAY_API_TEMPLATE, today.getYear())))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("祝日APIの取得に失敗しました。HTTPステータス: " + response.statusCode());
            }

            String body = response.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);

            for (JsonNode node : root) {
                String dateStr = node.get("date").asText(); // yyyy-MM-dd
                LocalDate date = LocalDate.parse(dateStr);
                holidays.add(date);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    public OriginalDate() {
        this.date = LocalDate.now();
    }
    public OriginalDate(final LocalDate date) {
        this.date = date;
    }
    public OriginalDate(final String dateStr) {
        for (DateTimeFormatter formatter : SUPPORTED_FORMATTERS) {
            try {
                this.date = LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 無視して次のパターンを試す
            }
        }
        if (this.date == null) {
            throw new IllegalArgumentException("対応していない日付形式です: " + dateStr);
        }
    }

    public boolean is土日() {
        DayOfWeek dayOfWeek = this.date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean is祝日() {
        return holidays.contains(this.date);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    /**
     * 引数で指定した月を足す
     *
     * @param months 月
     */
    public void plusMonths(final int months) {
        this.date = this.date.plusMonths(months);
    }

    /**
     * yyyy-MM-ddにする.
     *
     * @return yyyy-MM-dd
     */
    public String toYYYYMMDDwithHyphen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.date.format(formatter);
    }

    /**
     * yyyyMMddにする.
     *
     * @return yyyyMMdd
     */
    public String toYYYYMMDD() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return this.date.format(formatter);
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
