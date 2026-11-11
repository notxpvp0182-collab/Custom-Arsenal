package com.ahmed.customarsenal.ability;

public enum AbilityTrigger {
    SHIFT_LEFT_CLICK("shift_left", "Shift + LMB"),
    RIGHT_CLICK("right_click", "RMB"),
    SHIFT_RIGHT_CLICK("shift_right", "Shift + RMB"),
    ULTIMATE_KEY("slot_1", "[R] Ultimate"),
    SECONDARY_KEY("slot_2", "[G] Secondary"),
    UTILITY_KEY("slot_3", "[V] Utility");

    private final String slotId;
    private final String shortLabel;

    AbilityTrigger(String slotId, String shortLabel) {
        this.slotId = slotId;
        this.shortLabel = shortLabel;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public static AbilityTrigger fromSlotId(String slotId) {
        for (AbilityTrigger t : values()) {
            if (t.slotId.equalsIgnoreCase(slotId)) {
                return t;
            }
        }
        return ULTIMATE_KEY;
    }
}
