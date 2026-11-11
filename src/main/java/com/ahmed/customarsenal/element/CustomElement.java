package com.ahmed.customarsenal.element;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public record CustomElement(
        String id,
        String displayName,
        int primaryColor,
        ChatFormatting chatColor,
        ParticleOptions particle,
        ResourceLocation soundId,
        Holder<MobEffect> hitStatusEffect,
        int effectDurationTicks,
        float bonusElementalDamage,
        float cooldownModifier
) {
    public void applyElementalHit(LivingEntity target, LivingEntity attacker) {
        if (hitStatusEffect != null && effectDurationTicks > 0) {
            target.addEffect(new MobEffectInstance(hitStatusEffect, effectDurationTicks, 0));
        }
        if ("fire".equals(id)) {
            target.igniteForSeconds(4.0f);
        } else if ("ice".equals(id)) {
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze() + 60, 200));
        }
    }

    public SoundEvent resolveSound() {
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
        return event != null ? event : net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP;
    }
}
