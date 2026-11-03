package com.copel.productpackages.arena.selenium.service.unit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.TreeSet;

/**
 * 日本の祝日を計算で求めるクラス（外部APIに依存しない）.
 * 祝日法（2020年以降の規定）に基づき、振替休日・国民の休日も含めて算出する.
 */
public final class JapaneseHoliday {
    private JapaneseHoliday() {}

    /** 指定日が祝日（振替休日・国民の休日を含む）かどうか. */
    public static boolean isHoliday(final LocalDate date) {
        return holidaysOf(date.getYear()).contains(date);
    }

    /** 指定年の祝日一覧. */
    public static Set<LocalDate> holidaysOf(final int year) {
        TreeSet<LocalDate> base = new TreeSet<>();
        base.add(LocalDate.of(year, 1, 1));                       // 元日
        base.add(nthMonday(year, 1, 2));                          // 成人の日
        base.add(LocalDate.of(year, 2, 11));                      // 建国記念の日
        if (year >= 2020) base.add(LocalDate.of(year, 2, 23));    // 天皇誕生日
        base.add(LocalDate.of(year, 3, vernalEquinox(year)));     // 春分の日
        base.add(LocalDate.of(year, 4, 29));                      // 昭和の日
        base.add(LocalDate.of(year, 5, 3));                       // 憲法記念日
        base.add(LocalDate.of(year, 5, 4));                       // みどりの日
        base.add(LocalDate.of(year, 5, 5));                       // こどもの日
        base.add(nthMonday(year, 7, 3));                          // 海の日
        base.add(LocalDate.of(year, 8, 11));                      // 山の日
        base.add(nthMonday(year, 9, 3));                          // 敬老の日
        base.add(LocalDate.of(year, 9, autumnalEquinox(year)));   // 秋分の日
        base.add(nthMonday(year, 10, 2));                         // スポーツの日
        base.add(LocalDate.of(year, 11, 3));                      // 文化の日
        base.add(LocalDate.of(year, 11, 23));                     // 勤労感謝の日

        TreeSet<LocalDate> result = new TreeSet<>(base);
        // 振替休日: 祝日が日曜の場合、その後の最初の祝日でない日
        for (LocalDate d : base) {
            if (d.getDayOfWeek() == DayOfWeek.SUNDAY) {
                LocalDate s = d.plusDays(1);
                while (result.contains(s)) s = s.plusDays(1);
                result.add(s);
            }
        }
        // 国民の休日: 前日と翌日が祝日である平日
        for (LocalDate d : base) {
            LocalDate mid = d.plusDays(1);
            if (base.contains(mid.plusDays(1)) && !result.contains(mid) && mid.getDayOfWeek() != DayOfWeek.SUNDAY) {
                result.add(mid);
            }
        }
        return result;
    }

    private static LocalDate nthMonday(final int year, final int month, final int n) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.dayOfWeekInMonth(n, DayOfWeek.MONDAY));
    }

    private static int vernalEquinox(final int year) {
        return (int) (20.8431 + 0.242194 * (year - 1980) - Math.floor((year - 1980) / 4.0));
    }

    private static int autumnalEquinox(final int year) {
        return (int) (23.2488 + 0.242194 * (year - 1980) - Math.floor((year - 1980) / 4.0));
    }
}
