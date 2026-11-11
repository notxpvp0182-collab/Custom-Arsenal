package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.element.CustomElement;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

public record CustomItemData(
        String id,
        String displayName,
        CustomRarity customRarity,
        CustomItemCategory category,
        CustomElement element,
        double attackDamage,
        double attackSpeed,
        int maxDurability,
        int maxStackSize,
        Holder<MobEffect> passiveHeldEffect,
        List<CustomAbility> abilities
) {
    public CustomAbility getAbilityById(String abilityId) {
        for (CustomAbility ability : abilities) {
            if (ability.id().equals(abilityId)) {
                return ability;
            }
        }
        return null;
    }

    public CustomAbility getAbilityBySlot(String keySlot) {
        for (CustomAbility ability : abilities) {
            if (ability.keySlot().equals(keySlot)) {
                return ability;
            }
        }
        return null;
    }
}
