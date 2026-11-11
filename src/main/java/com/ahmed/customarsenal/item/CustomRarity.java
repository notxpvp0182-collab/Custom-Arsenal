package com.ahmed.customarsenal.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;

public enum CustomRarity {
    COMMON("Common", ChatFormatting.WHITE, 0xFFFFFF, Rarity.COMMON),
    UNCOMMON("Uncommon", ChatFormatting.GREEN, 0x55FF55, Rarity.UNCOMMON),
    RARE("Rare", ChatFormatting.AQUA, 0x55FFFF, Rarity.RARE),
    EPIC("Epic", ChatFormatting.LIGHT_PURPLE, 0xFF55FF, Rarity.EPIC),
    LEGENDARY("Legendary", ChatFormatting.GOLD, 0xFFAA00, Rarity.EPIC),
    MYTHIC("Mythic", ChatFormatting.RED, 0xFF5555, Rarity.EPIC),
    ANCIENT("Ancient", ChatFormatting.DARK_RED, 0xAA0000, Rarity.EPIC);

    private final String displayName;
    private final ChatFormatting formatting;
    private final int colorHex;
    private final Rarity vanillaRarity;

    CustomRarity(String displayName, ChatFormatting formatting, int colorHex, Rarity vanillaRarity) {
        this.displayName = displayName;
        this.formatting = formatting;
        this.colorHex = colorHex;
        this.vanillaRarity = vanillaRarity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    public int getColorHex() {
        return colorHex;
    }

    public Rarity getVanillaRarity() {
        return vanillaRarity;
    }
}
