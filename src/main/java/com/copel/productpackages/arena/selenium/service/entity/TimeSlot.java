package com.copel.productpackages.arena.selenium.service.entity;

import com.copel.productpackages.arena.selenium.service.entity.unit.OriginalDateTime;

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
}
