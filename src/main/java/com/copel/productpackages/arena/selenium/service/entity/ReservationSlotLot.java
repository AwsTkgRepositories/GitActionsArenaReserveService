package com.copel.productpackages.arena.selenium.service.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ReservationSlotLot implements Iterable<ReservationSlot> {
    /**
     * Lotオブジェクト.
     */
    private Set<ReservationSlot> slotLot;

    public ReservationSlotLot() {
        this.slotLot = new HashSet<ReservationSlot>();
    }

    public Iterator<ReservationSlot> iterator() {
        return this.slotLot.iterator();
    }

    public void add(final ReservationSlot slot) {
        this.slotLot.add(slot);
    }

    public int size() {
        return this.slotLot.size();
    }

    public boolean isTargetExists() {
        return this.slotLot.stream()
                .anyMatch(slot -> slot.is予約可能() && (slot.is土日祝日() || slot.is平日夜間()));
    }

    /**
     * このLotから土日祝日のみのものを作成する.
     *
     * @return ReservationSlotLot
     */
    public ReservationSlotLot filter土日祝日Only() {
        ReservationSlotLot result = new ReservationSlotLot();
        for (ReservationSlot slot : this.slotLot) {
            if (slot.is土日祝日()) {
                result.add(slot);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (final ReservationSlot slot : this.slotLot) {
            if (slot.is予約可能() && (slot.is土日祝日() || slot.is平日夜間())) {
                stringBuilder.append(slot);
                stringBuilder.append("\\n");
            }
        }
        return stringBuilder.toString();
    }
}
