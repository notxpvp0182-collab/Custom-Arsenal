package com.ahmed.customarsenal.util;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.item.CustomItemData;

public final class AssetValidator {

    public static boolean validateItemData(CustomItemData data) {
        if (data == null) {
            CustomArsenalMod.LOGGER.error("CustomItemData is null!");
            return false;
        }
        if (data.id() == null || data.id().isBlank()) {
            CustomArsenalMod.LOGGER.error("CustomItemData has a blank ID!");
            return false;
        }
        if (data.element() == null) {
            CustomArsenalMod.LOGGER.warn("CustomItem '{}' is missing an Element; defaulting gracefully.", data.id());
        }
        if (data.maxStackSize() < 1 || data.maxStackSize() > 64) {
            CustomArsenalMod.LOGGER.error("CustomItem '{}' has invalid stack size {}.", data.id(), data.maxStackSize());
            return false;
        }
        for (CustomAbility ability : data.abilities()) {
            if (!validateAbility(ability)) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateAbility(CustomAbility ability) {
        if (ability == null || ability.id() == null || ability.id().isBlank()) {
            CustomArsenalMod.LOGGER.error("Invalid ability with null or empty ID!");
            return false;
        }
        if (ability.cooldownTicks() < 0) {
            CustomArsenalMod.LOGGER.error("Ability '{}' has negative cooldown ({}).", ability.id(), ability.cooldownTicks());
            return false;
        }
        if (ability.keySlot() == null || ability.keySlot().isBlank()) {
            CustomArsenalMod.LOGGER.error("Ability '{}' has invalid keybind slot reference.", ability.id());
            return false;
        }
        return true;
    }
}
