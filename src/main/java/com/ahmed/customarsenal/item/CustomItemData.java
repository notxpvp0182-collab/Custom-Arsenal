package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.element.CustomElement;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Rarity;

import java.util.List;

public record CustomItemData(
        String id,
        String displayName,
        List<String> lore,
        Rarity rarity,
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
}
