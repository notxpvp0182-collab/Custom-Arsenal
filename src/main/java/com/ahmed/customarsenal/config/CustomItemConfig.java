package com.ahmed.customarsenal.config;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.ability.ActivationHand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CustomItemConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("custom_arsenal.json");
    private static CustomItemConfig INSTANCE = new CustomItemConfig();

    public boolean enableCustomParticles = true;
    public float particleIntensity = 1.0f; // 0.25f to 2.0f
    public boolean enableCustomSounds = true;
    public boolean enableAbilityVisualEffects = true;
    public boolean enableAbilityAnimations = true;
    public boolean showCooldownIndicators = true;
    public boolean enableCustom3DRendering = true;

    // Ability ID -> Override ActivationHand name ("MAIN_HAND", "OFF_HAND", "BOTH")
    public Map<String, String> activationHandOverrides = new HashMap<>();
    // Ability ID -> enabled state
    public Map<String, Boolean> abilityEnabledStates = new HashMap<>();

    public static CustomItemConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                CustomItemConfig loaded = GSON.fromJson(reader, CustomItemConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                }
            } catch (IOException e) {
                CustomArsenalMod.LOGGER.error("Failed to read Custom Arsenal config, using defaults.", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            CustomArsenalMod.LOGGER.error("Failed to save Custom Arsenal config.", e);
        }
    }

    public ActivationHand getEffectiveHand(String abilityId, ActivationHand defaultHand) {
        String override = activationHandOverrides.get(abilityId);
        if (override != null) {
            try {
                return ActivationHand.valueOf(override);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return defaultHand;
    }

    public boolean isAbilityEnabled(String abilityId) {
        return abilityEnabledStates.getOrDefault(abilityId, true);
    }
}
