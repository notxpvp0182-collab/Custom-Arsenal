package com.ahmed.customarsenal.ability;

import com.ahmed.customarsenal.config.CustomItemConfig;
import com.ahmed.customarsenal.element.CustomElement;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Predicate;

public record CustomAbility(
        String id,
        String displayName,
        String description,
        String keySlot, // "slot_1" (R), "slot_2" (G), "slot_3" (V), "shield" (C)
        int cooldownTicks,
        ActivationHand defaultHand,
        CustomElement element,
        SoundEvent sound,
        double range,
        int durationTicks,
        Predicate<AbilityContext> condition,
        AbilityExecutor executor
) {
    @FunctionalInterface
    public interface AbilityExecutor {
        boolean execute(AbilityContext context);
    }

    public ActivationHand getEffectiveHand() {
        return CustomItemConfig.get().getEffectiveHand(id, defaultHand);
    }

    public int getEffectiveCooldownTicks() {
        float mod = element != null ? element.cooldownModifier() : 1.0f;
        return Math.max(5, Math.round(cooldownTicks * mod));
    }
}
