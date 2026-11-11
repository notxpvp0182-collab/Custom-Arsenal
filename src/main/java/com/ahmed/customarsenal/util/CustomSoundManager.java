package com.ahmed.customarsenal.util;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.config.CustomItemConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public final class CustomSoundManager {
    public static final ResourceLocation SHADOW_DASH_ID = CustomArsenalMod.id("shadow_dash");
    public static final ResourceLocation SHADOW_STRIKE_ID = CustomArsenalMod.id("shadow_strike");
    public static final ResourceLocation FIRE_SLASH_ID = CustomArsenalMod.id("fire_slash");
    public static final ResourceLocation FROST_WAVE_ID = CustomArsenalMod.id("frost_wave");
    public static final ResourceLocation THUNDER_STRIKE_ID = CustomArsenalMod.id("thunder_strike");
    public static final ResourceLocation VOID_PULSE_ID = CustomArsenalMod.id("void_pulse");
    public static final ResourceLocation GUARDIAN_BARRIER_ID = CustomArsenalMod.id("guardian_barrier");
    public static final ResourceLocation GRAPPLE_LAUNCH_ID = CustomArsenalMod.id("grapple_launch");
    public static final ResourceLocation WIND_DASH_ID = CustomArsenalMod.id("wind_dash");
    public static final ResourceLocation POISON_STRIKE_ID = CustomArsenalMod.id("poison_strike");
    public static final ResourceLocation LIGHT_BURST_ID = CustomArsenalMod.id("light_burst");

    public static final SoundEvent SHADOW_DASH = register(SHADOW_DASH_ID);
    public static final SoundEvent SHADOW_STRIKE = register(SHADOW_STRIKE_ID);
    public static final SoundEvent FIRE_SLASH = register(FIRE_SLASH_ID);
    public static final SoundEvent FROST_WAVE = register(FROST_WAVE_ID);
    public static final SoundEvent THUNDER_STRIKE = register(THUNDER_STRIKE_ID);
    public static final SoundEvent VOID_PULSE = register(VOID_PULSE_ID);
    public static final SoundEvent GUARDIAN_BARRIER = register(GUARDIAN_BARRIER_ID);
    public static final SoundEvent GRAPPLE_LAUNCH = register(GRAPPLE_LAUNCH_ID);
    public static final SoundEvent WIND_DASH = register(WIND_DASH_ID);
    public static final SoundEvent POISON_STRIKE = register(POISON_STRIKE_ID);
    public static final SoundEvent LIGHT_BURST = register(LIGHT_BURST_ID);

    private static SoundEvent register(ResourceLocation id) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
        if (!CustomItemConfig.get().enableCustomSounds || sound == null) return;
        level.playSound(null, x, y, z, sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static void init() {
        // Static initialization registers all sounds
    }
}
