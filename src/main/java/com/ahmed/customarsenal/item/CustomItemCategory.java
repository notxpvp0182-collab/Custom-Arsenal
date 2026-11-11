package com.ahmed.customarsenal.item;

public enum CustomItemCategory {
    ALL("All"),
    WEAPONS("Weapons"),
    ARMOR("Armor"),
    SHIELDS("Shields"),
    MAGIC("Magic"),
    ELEMENTS("Elements"),
    TOOLS("Tools"),
    SPECIAL("Special"),
    UTILITY("Utility");

    private final String displayName;

    CustomItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
