package com.copel.productpackages.arena.selenium.service.entity;

import java.util.Objects;

import com.copel.productpackages.arena.selenium.service.ota.大田区体育館;
import com.copel.productpackages.arena.selenium.service.ota.大田区時間帯;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDateTime;

/**
 * 時間帯クラス.
 *
 * @author 鈴木一矢
 *
 */
public class TimeSlot {
    /**
     * 枠名.
     */
    private String slotName;
    /**
     * 開始時刻.
     */
    private final OriginalDateTime startTime;
    /**
     * 終了時刻.
     */
    private final OriginalDateTime endTime;

    /**
     * コンストラクタ.
     *
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     */
    public TimeSlot(final String slotName, final OriginalDateTime startTime, final OriginalDateTime endTime) {
        this.slotName = slotName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 枠名から大田区のTimeSlotを取得する
     *
     * @param slotName
     * @return
     */
    public static TimeSlot getTimeSlotOtaByName(final 大田区時間帯 時間帯, final 大田区体育館 体育館, final OriginalDate date) {
        TimeSlot slot = null;
        switch (体育館) {
            case 田園調布せせらぎ館:
                switch (時間帯) {
                    case 午前 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 9, 0, 0), new OriginalDateTime(date, 11, 30, 0));
                        break;
                    case 午後1 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 12, 30, 0), new OriginalDateTime(date, 15, 00, 0));
                        break;
                    case 午後2 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 16, 0, 0), new OriginalDateTime(date, 18, 30, 0));
                        break;
                    case 夜間 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 19, 30, 0), new OriginalDateTime(date, 22, 0, 0));
                        break;
                    default:
                        break;
                }
            case 大森スポーツセンター:
                switch (時間帯) {
                    case 午前 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 9, 0, 0), new OriginalDateTime(date, 12, 0, 0));
                        break;
                    case 午後1 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 13, 0, 0), new OriginalDateTime(date, 17, 00, 0));
                        break;
                    case 夜間 :
                        slot = new TimeSlot(時間帯.name(), new OriginalDateTime(date, 18, 0, 0), new OriginalDateTime(date, 22, 0, 0));
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return slot;
    }

    /**
     * 枠名から世田谷区のTimeSlotを取得する
     *
     * @param slotName
     * @return
     */
    public static TimeSlot getTimeSlotSetagayaByName(final String slotName, final OriginalDate date) {
        TimeSlot slot = null;
        switch (slotName) {
            case "午前" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 9, 0, 0), new OriginalDateTime(date, 11, 0, 0));
                break;
            case "午後" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 11, 30, 0), new OriginalDateTime(date, 16, 00, 0));
                break;
            case "午後1" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 11, 30, 0), new OriginalDateTime(date, 13, 30, 0));
                break;
            case "午後2" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 14, 0, 0), new OriginalDateTime(date, 16, 0, 0));
                break;
            case "午後3" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 16, 30, 0), new OriginalDateTime(date, 18, 30, 0));
                break;
            case "夜間" :
                slot = new TimeSlot(slotName, new OriginalDateTime(date, 19, 0, 0), new OriginalDateTime(date, 22, 0, 0));
                break;
            default:
                break;
        }
        return slot;
    }

    /**
     * この時間帯が夜間(18:00以降)であるかどうかを判定する.
     *
     * @return 夜間であればtrue、それ以外はfalse
     */
    public boolean is夜間() {
        return this.startTime.isAfterHour(18) || this.endTime.isAfterHour(20);
    }

    public OriginalDateTime getStartTime() {
        return this.startTime;
    }

    public OriginalDateTime getEndTime() {
        return this.endTime;
    }

    public String toDisplayString() {
        return this.slotName + "(" + this.startTime.getHHmm() + "〜" + this.endTime.getHHmm() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimeSlot other = (TimeSlot) obj;
        return Objects.equals(endTime, other.endTime) && Objects.equals(slotName, other.slotName)
                && Objects.equals(startTime, other.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endTime, slotName, startTime);
    }
}
