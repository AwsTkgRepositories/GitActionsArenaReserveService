package com.copel.productpackages.arena.selenium.service.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReservationSlotLot implements Iterable<ReservationSlot> {
    /**
     * Lotオブジェクト.
     */
    private List<ReservationSlot> slotLot;

    public ReservationSlotLot() {
        this.slotLot = new ArrayList<ReservationSlot>();
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
