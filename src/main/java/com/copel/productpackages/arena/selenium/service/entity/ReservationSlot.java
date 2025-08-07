package com.copel.productpackages.arena.selenium.service.entity;

import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;

import lombok.Data;

/**
 * 予約枠クラス.
 *
 * @author 鈴木一矢
 *
 */
@Data
public class ReservationSlot implements Comparable<ReservationSlot> {
    /**
     * 日付.
     */
    private OriginalDate slotDate;
    /**
     * 時間帯.
     */
    private TimeSlot timeSlot;
    /**
     * コート大きさ.
     */
    private CourtUsageType usageType;
    /**
     * 予約状態区分.
     */
    private ReserveStatus reserveStatus;

    /**
     * 予約可能であるかを判定する.
     *
     * @return 予約可能であればtrue、そうでなければfalse
     */
    public boolean is予約可能() {
        return ReserveStatus.予約可能.equals(this.reserveStatus);
    }

    /**
     * この枠が予約閲覧可能な対象であるかどうかを判定する.
     *
     * @return 対象であればtrue、そうでなければfalse
     */
    public boolean is予約閲覧対象() {
        return !ReserveStatus.その他.equals(this.reserveStatus);
    }

    /**
     * この枠が土日または祝日であるかどうかを判定する.
     *
     * @return 土日祝日であればtrue、そうでなければfalse
     */
    public boolean is土日祝日() {
        return this.slotDate.is土日() || this.slotDate.is祝日();
    }

    /**
     * この枠が平日の夜間であるかどうかを判定する.
     *
     * @return 平日の夜間であればtrue、そうでなければfalse
     */
    public boolean is平日夜間() {
        return !this.slotDate.is土日() && this.timeSlot.is夜間();
    }

    @Override
    public String toString() {
        return this.slotDate.toDisplayStringWithoutYear() + "：" + this.timeSlot.toDisplayString() + this.usageType.name() + " " + this.reserveStatus.getIcon();
    }

    @Override
    public int compareTo(ReservationSlot o) {
        return this.slotDate.compareTo(o.getSlotDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservationSlot that = (ReservationSlot) o;

        if (this.slotDate != null ? !this.slotDate.equals(that.getSlotDate()) : that.getSlotDate() != null) return false;
        if (this.timeSlot != null ? !this.timeSlot.equals(that.getTimeSlot()) : that.getTimeSlot() != null) return false;
        if (this.usageType != that.getUsageType()) return false;
        return this.reserveStatus == that.getReserveStatus();
    }

    @Override
    public int hashCode() {
        int result = this.slotDate != null ? this.slotDate.hashCode() : 0;
        result = 31 * result + (this.timeSlot != null ? this.timeSlot.hashCode() : 0);
        result = 31 * result + (this.usageType != null ? this.usageType.hashCode() : 0);
        result = 31 * result + (this.reserveStatus != null ? this.reserveStatus.hashCode() : 0);
        return result;
    }
}
