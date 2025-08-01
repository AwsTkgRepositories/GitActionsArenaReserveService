package com.copel.productpackages.arena.selenium.service.unit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * 【フレームワーク部品】
 * 日時の情報を持つクラス
 * 
 * @author 鈴木一矢
 *
 */
public class OriginalDateTime implements Comparable<OriginalDateTime> {
    // ================================
    // フィールド定義
    // ================================
    /**
     * 日時
     */
    protected LocalDateTime dateTime;

    // ================================
    // コンストラクタ
    // ================================
    /**
     * 現在日時でこのオブジェクトを初期化
     */
    public OriginalDateTime() {
        this.dateTime = LocalDateTime.now();
    }

    public OriginalDateTime(final OriginalDateTime originalDateTime) {
        this(originalDateTime != null ? originalDateTime.getYyyyMMdd() : null);
    }

    /**
     * 文字列の日付情報を解析しこのオブジェクトを初期化する
     * @param date 文字列の日付情報
     */
    public OriginalDateTime(String date) {
        if (date == null) {
            this.dateTime = null;
        } else {
            // パースを試みるパターンのリスト
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss.SSSSSS", 
                "yyyy-MM-dd HH:mm:ss.SSSSS", 
                "yyyy-MM-dd HH:mm:ss.SSSS", 
                "yyyy-MM-dd HH:mm:ss.SSS", 
                "yyyy-MM-dd HH:mm:ss.SS", 
                "yyyy-MM-dd HH:mm:ss.S", 
                "yyyy-MM-dd HH:mm:ss", 
                "yyyy-MM-dd HH:mm", 
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss.SSSSSS",
                "yyyy/MM/dd HH:mm:ss.SSSSS",
                "yyyy/MM/dd HH:mm:ss.SSSS",
                "yyyy/MM/dd HH:mm:ss.SSS",
                "yyyy/MM/dd HH:mm:ss.SS",
                "yyyy/MM/dd HH:mm:ss.S",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm",
                "yyyy/MM/dd"
            };
            // パースを試みる
            for (String pattern : patterns) {
                try {
                    if (pattern.equals("yyyy-MM-dd") || pattern.equals("yyyy/MM/dd")) {
                        this.dateTime = LocalDateTime.parse(date + " 00:00:00", DateTimeFormatter.ofPattern(pattern + " HH:mm:ss"));
                    } else {
                        this.dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
                    }
                    break;
                } catch (DateTimeParseException e) {
                    // パースに失敗した場合、次のパターンを試す
                }
            }
        }
    }

    /**
     * 年月日時分秒でこのオブジェクトを初期化する
     */
    public OriginalDateTime(int year, int month, int date, int hour, int minutes, int second) {
        this.dateTime = LocalDateTime.of(year, month, date, hour, minutes, second);
    }

    /**
     * java.sql.Dateをjava.time.LocalDateTimeに変換
     * @param date
     */
    public OriginalDateTime(java.sql.Date date) {
        if (date == null) {
            this.dateTime = null;
        } else {
            this.dateTime = date.toLocalDate().atStartOfDay();
        }
    }

    /**
     *  java.sql.Timestampをjava.time.LocalDateTimeに変換
     * @param timestamp
     */
    public OriginalDateTime(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            this.dateTime = null;
        } else {
            this.dateTime = timestamp.toLocalDateTime();
        }
    }

    /**
     * OriginalDateと時刻情報でこのオブジェクトを初期化する
     * @param date OriginalDateインスタンス
     * @param hour 時
     * @param minutes 分
     * @param second 秒
     */
    public OriginalDateTime(final OriginalDate date, int hour, int minutes, int second) {
        if (date == null) {
            this.dateTime = null;
        } else {
            LocalDate localDate = date.getDate();
            this.dateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), hour, minutes, second);
        }
    }

    // ================================
    // メソッド定義
    // ================================
    @Override
    public String toString() {
        if (this.dateTime == null) {
            return null;
        }
        return this.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int compareTo(OriginalDateTime targetDateTime) {
        if (this.equals(targetDateTime)) {
            return 0;
        }
        if (this.dateTime != null && targetDateTime != null) {
            return this.dateTime.isAfter(targetDateTime.toLocalDateTime()) ? 1 : -1;
        } else {
            return -1;
        }
    }

    /**
     * 引数に指定した時刻(H)以降であるかどうかを判定する.
     *
     * @param hour 時刻
     * @return 引数に指定した時刻(H)以降であればtrue、それ以外はfalse
     */
    public boolean isAfterHour(int hour) {
        OriginalDateTime targetDateTime = new OriginalDateTime(this);
        targetDateTime.setHour(hour);
        targetDateTime.setMinute(0);
        return this.dateTime.isAfter(targetDateTime.toLocalDateTime());
    }

    public void setHour(int hour) {
        this.dateTime = this.dateTime.withHour(hour);
    }

    public void setMinute(int minute) {
        this.dateTime = this.dateTime.withMinute(minute);
    }

    public int getYear() {
        return this.dateTime.getYear();
    }

    /**
     * このオブジェクトをOriginalDate型に変換し返却します.
     *
     * @return OriginalDateインスタンス
     */
    public OriginalDate toOriginalDate() {
        return new OriginalDate(this.dateTime.toLocalDate());
    }

    /**
     * このオブジェクトが空であるかどうかを判定します.
     *
     * @return this.dateTimeがNULLであればtrue、そうでなければfalseを返却する
     */
    public boolean isEmpty() {
        return this.dateTime == null;
    }

    /**
     * 引数のDateTimeオブジェクトとこのオブジェクトを比較し、日付の値が等しい場合はtrueを返却する
     *
     * @param dateTime 比較対象のDateTimeオブジェクト
     * @return 比較結果
     */
    public boolean equals(final OriginalDateTime dateTime) {
        return this.dateTime != null ? this.dateTime.equals(dateTime.toLocalDateTime()) : false ;
    }

    /**
     * このオブジェクトが持つ日時の曜日文字列を返却します。
     * @return (月)などの形式の曜日文字列
     */
    public String get曜日() {
        if (this.dateTime == null) {
            return null;
        }
        // パターンとロケールを指定してDateTimeFormatterを作成
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(E)", Locale.JAPANESE);
        // LocalDateTimeオブジェクトを指定したパターンでフォーマットし、曜日の文字列を取得
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ日時をMM/dd形式で返却します。
     * @return MM/dd形式の日付を表す文字列
     */
    public String getMMdd() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してMM/dd形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        // MM/dd形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ時刻をHH:mm形式で返却します。
     * @return HH:mm形式の日付を表す文字列
     */
    public String getHHmm() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してMM/dd形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // MM/dd形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ時刻をHH:mm形式で返却します。
     * @return HH:mm形式の日付を表す文字列
     */
    public String getHHmmss() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してHH:mm:ss形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        // HH:mm:ss形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ日時をyyyy/MM/dd形式で返却します。
     * @return yyyy/MM/dd形式の日付を表す文字列
     */
    public String getYyyyMMdd() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してMM/dd形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        // yyyy/MM/dd形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ日時をyyyyMMdd形式で返却します。
     * @return yyyyMMdd形式の日付を表す文字列
     */
    public String getYyyyMMddWithoutSlash() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してyyyyMMdd形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // yyyy/MM/dd形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが待つ日時をyyyy-MM-dd形式で返却します。
     * @return yyyy-MM-dd形式の日付を表す文字列
     */
    public String getYyyy_MM_dd() {
        if (this.dateTime == null) {
            return null;
        }
        // DateTimeFormatterを使用してyyyy-MM-dd形式にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // yyyy/MM/dd形式で日付を返却
        return this.dateTime.format(formatter);
    }

    /**
     * このオブジェクトが持つ日付の情報をLocalDateTime型で返却します
     * @return LocalDateTime型の日付情報
     */
    public LocalDateTime toLocalDateTime() {
        return this.dateTime;
    }

    /**
     * このオブジェクトが持つ日付の情報をLocalDate型で返却します
     * @return LocalDate型の日付情報
     */
    public LocalDate toLocalDate() {
        return this.dateTime != null ? this.dateTime.toLocalDate() : null;
    }

    /**
     * このオブジェクトが持つ日付の情報をjava.sql.Timestamp型で返却します
     * @return java.sql.Timestamp型の日付情報
     */
    public java.sql.Timestamp toTimestamp() {
        return this.dateTime != null ? java.sql.Timestamp.valueOf(this.dateTime) : null;
    }

    /**
     * このオブジェクトが持つ日付と引数の日付の差の日数を返却します
     * @param 比べたい日付
     * @return 日付の差の日数（負の数が返却された場合はエラー）
     */
    public int betweenDays(OriginalDateTime date) {
        if (date != null && this.dateTime != null) {
            // 両日付のLocalDateを取得
            LocalDate startDate = this.dateTime.toLocalDate();
            LocalDate endDate = date.toLocalDate();

            // 日付の差を計算
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

            return (int)daysBetween;
        } else {
            return -1;
        }
    }

    /**
     * このオブジェクトが持つ日付と引数の日付の差の月数を返却します
     * @param 比べたい日付
     * @return 日付の差の月数（負の数が返却された場合はエラー）
     */
    public int betweenMonth(OriginalDateTime date) {
        if (date != null && this.dateTime != null) {
            // 両日付のLocalDateを取得
            LocalDate startDate = this.dateTime.toLocalDate();
            LocalDate endDate = date.toLocalDate();

            // 期間を年と月で計算
            Period period = Period.between(startDate, endDate);

            // 年を月に換算し、月の差と合わせる
            int totalMonths = period.getYears() * 12 + period.getMonths();

            return totalMonths;
        } else {
            return -1;
        }
    }

    /**
     * このオブジェクトが持つ日付と引数の日付の差の年数を返却します
     * @param 比べたい日付
     * @return 日付の差の年数（負の数が返却された場合はエラー）
     */
    public int betweenYear(OriginalDateTime date) {
        if (date != null && this.dateTime != null) {
            Period period = Period.between(this.dateTime.toLocalDate(), date.toLocalDate());
            return period.getYears();
        } else {
            return -1;   
        }
    }

    /**
     * このDateTimeに引数日プラスします.
     *
     * @param days 足す日数
     */
    public void plusDays(final int days) {
        if (this.dateTime != null) {
            this.dateTime = this.dateTime.plusDays(days);
        }
    }

    /**
     * このDateTimeから引数分引きます.
     *
     * @param minutes 引く分数
     */
    public void minusMinutes(final int minutes) {
        if (this.dateTime != null) {
            this.dateTime = this.dateTime.minusMinutes(minutes);
        } else {
            this.dateTime = LocalDateTime.now();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OriginalDateTime other = (OriginalDateTime) obj;
        return Objects.equals(dateTime, other.dateTime);
    }
}
