package com.ahmed.customarsenal.element;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.util.CustomSoundManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ElementRegistry {
    private static final Map<String, CustomElement> ELEMENTS = new LinkedHashMap<>();

    public static final CustomElement FIRE = register(new CustomElement(
            "fire", "Fire", 0xFF5500, ChatFormatting.GOLD,
            ParticleTypes.FLAME, CustomSoundManager.FIRE_SLASH_ID,
            null, 80, 2.5f, 1.0f
    ));

    public static final CustomElement ICE = register(new CustomElement(
            "ice", "Ice", 0x66CCFF, ChatFormatting.AQUA,
            ParticleTypes.SNOWFLAKE, CustomSoundManager.FROST_WAVE_ID,
            MobEffects.SLOWNESS, 80, 1.5f, 1.0f
    ));

    public static final CustomElement LIGHTNING = register(new CustomElement(
            "lightning", "Lightning", 0xFFEE44, ChatFormatting.YELLOW,
            ParticleTypes.ELECTRIC_SPARK, CustomSoundManager.THUNDER_STRIKE_ID,
            MobEffects.GLOWING, 60, 3.0f, 0.95f
    ));

    public static final CustomElement WIND = register(new CustomElement(
            "wind", "Wind", 0xAAFFCC, ChatFormatting.GREEN,
            ParticleTypes.CLOUD, CustomSoundManager.WIND_DASH_ID,
            MobEffects.SPEED, 60, 1.0f, 0.85f
    ));

    public static final CustomElement EARTH = register(new CustomElement(
            "earth", "Earth", 0x8B5A2B, ChatFormatting.DARK_GREEN,
            ParticleTypes.CRIT, CustomSoundManager.GUARDIAN_BARRIER_ID,
            MobEffects.RESISTANCE, 80, 2.0f, 1.1f
    ));

    public static final CustomElement WATER = register(new CustomElement(
            "water", "Water", 0x2266FF, ChatFormatting.BLUE,
            ParticleTypes.SPLASH, CustomSoundManager.FROST_WAVE_ID,
            MobEffects.CONDUIT_POWER, 80, 1.5f, 0.95f
    ));

    public static final CustomElement SHADOW = register(new CustomElement(
            "shadow", "Shadow", 0x551A8B, ChatFormatting.DARK_PURPLE,
            ParticleTypes.PORTAL, CustomSoundManager.SHADOW_DASH_ID,
            MobEffects.BLINDNESS, 40, 2.5f, 0.9f
    ));

    public static final CustomElement LIGHT = register(new CustomElement(
            "light", "Light", 0xFFFFAA, ChatFormatting.WHITE,
            ParticleTypes.END_ROD, CustomSoundManager.LIGHT_BURST_ID,
            MobEffects.GLOWING, 100, 2.0f, 0.95f
    ));

    public static final CustomElement POISON = register(new CustomElement(
            "poison", "Poison", 0x33AA33, ChatFormatting.DARK_GREEN,
            ParticleTypes.ITEM_SLIME, CustomSoundManager.POISON_STRIKE_ID,
            MobEffects.POISON, 100, 1.5f, 0.9f
    ));

    public static final CustomElement VOID = register(new CustomElement(
            "void", "Void", 0x2B0040, ChatFormatting.DARK_GRAY,
            ParticleTypes.REVERSE_PORTAL, CustomSoundManager.VOID_PULSE_ID,
            MobEffects.WITHER, 60, 3.5f, 1.05f
    ));

    public static CustomElement register(CustomElement element) {
        if (element == null || element.id() == null || element.id().isBlank()) {
            CustomArsenalMod.LOGGER.error("Attempted to register an invalid CustomElement!");
            return null;
        }
        if (ELEMENTS.containsKey(element.id())) {
            CustomArsenalMod.LOGGER.warn("Duplicate CustomElement ID '{}' ignored.", element.id());
            return ELEMENTS.get(element.id());
        }
        ELEMENTS.put(element.id(), element);
        return element;
    }

    public static CustomElement get(String id) {
        if (id == null) return SHADOW;
        CustomElement el = ELEMENTS.get(id);
        if (el == null) {
            CustomArsenalMod.LOGGER.warn("Unknown element ID '{}', falling back to Shadow.", id);
            return SHADOW;
        }
        return el;
    }

    public static Collection<CustomElement> getAll() {
        return Collections.unmodifiableCollection(ELEMENTS.values());
    }

    public static int size() {
        return ELEMENTS.size();
    }

    public static void init() {
    }
}
