package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.ability.AbilityRegistry;
import com.ahmed.customarsenal.element.CustomElement;
import com.ahmed.customarsenal.element.ElementRegistry;
import com.ahmed.customarsenal.util.AssetValidator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

import java.util.*;

public final class CustomItemRegistry {
    private static final Map<String, CustomItem> ITEMS = new LinkedHashMap<>();

    // ==================== SWORDS & BLADES ====================
    public static final CustomItem SHADOW_SWORD = reg("shadow_sword", "Shadow Sword", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.SHADOW, 11.0, 1.6, 2200, MobEffects.NIGHT_VISION,
            "Shadow Slash", 80, "SLASH", 9.0f, "Shadow Dash", 100, "DASH", 8.5f, "Shadow Teleport", 160, "TELEPORT", 10.0f, "Shadow Apocalypse", 500, "ULTIMATE_CATACLYSM", 18.0f);

    public static final CustomItem INFERNO_SWORD = reg("inferno_sword", "Inferno Sword", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 10.5, 1.6, 2000, MobEffects.FIRE_RESISTANCE,
            "Inferno Slash", 80, "SLASH", 9.5f, "Flame Dash", 100, "DASH", 8.0f, "Fireball Barrage", 140, "PROJECTILE", 10.0f, "Inferno Cataclysm", 500, "ULTIMATE_CATACLYSM", 18.5f);

    public static final CustomItem FROST_BLADE = reg("frost_blade", "Frost Blade", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.ICE, 10.0, 1.65, 1950, null,
            "Ice Cleave", 80, "SLASH", 8.5f, "Frost Wave", 110, "WAVE", 9.0f, "Glacial Dash", 130, "DASH", 8.5f, "Blizzard Zero", 500, "ULTIMATE_CATACLYSM", 17.0f);

    public static final CustomItem WIND_BLADE = reg("wind_blade", "Wind Blade", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.WIND, 9.0, 2.0, 1800, MobEffects.SPEED,
            "Gale Cut", 60, "SLASH", 7.5f, "Wind Dash", 80, "DASH", 7.5f, "Cyclone Wave", 120, "WAVE", 8.5f, "Tempest Slash", 440, "ULTIMATE_CATACLYSM", 16.0f);

    public static final CustomItem CELESTIAL_SWORD = reg("celestial_sword", "Celestial Sword", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.LIGHT, 12.0, 1.7, 2600, MobEffects.REGENERATION,
            "Radiant Slash", 80, "SLASH", 10.5f, "Solar Dash", 100, "DASH", 9.5f, "Heavenly Beam", 140, "PROJECTILE", 11.0f, "Solar Supernova", 520, "ULTIMATE_CATACLYSM", 20.0f);

    public static final CustomItem DRAGON_BLADE = reg("dragon_blade", "Dragon Blade", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 12.5, 1.6, 2800, MobEffects.STRENGTH,
            "Draconic Cleave", 80, "SLASH", 11.0f, "Dragon Flight", 100, "DASH", 9.5f, "Dragon Breath", 140, "PROJECTILE", 11.5f, "Dragon Roar", 540, "ULTIMATE_CATACLYSM", 21.0f);

    public static final CustomItem LAVA_BLADE = reg("lava_blade", "Lava Blade", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 10.0, 1.5, 1850, MobEffects.FIRE_RESISTANCE,
            "Magma Slash", 80, "SLASH", 9.0f, "Lava Surge", 110, "WAVE", 9.5f, "Molten Slam", 140, "SLAM", 10.0f, "Volcanic Eruption", 480, "ULTIMATE_CATACLYSM", 17.5f);

    public static final CustomItem FROSTMOURNE_BLADE = reg("frostmourne_blade", "Frostmourne Blade", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.ICE, 12.0, 1.55, 2700, MobEffects.RESISTANCE,
            "Soul Frost Slash", 80, "SLASH", 10.5f, "Frozen Pull", 120, "VORTEX", 10.0f, "Ice Spike Wave", 150, "WAVE", 11.0f, "Absolute Zero", 540, "ULTIMATE_CATACLYSM", 20.5f);

    // ==================== DAGGERS ====================
    public static final CustomItem POISON_DAGGER = reg("poison_dagger", "Poison Dagger", CustomRarity.EPIC, CustomItemCategory.SPECIAL, ElementRegistry.POISON, 8.0, 2.3, 1500, null,
            "Venom Fang", 60, "SLASH", 7.5f, "Toxic Lunge", 80, "DASH", 7.5f, "Plague Cloud", 120, "WAVE", 8.5f, "Biohazard Burst", 420, "ULTIMATE_CATACLYSM", 15.5f);

    public static final CustomItem SHADOW_DAGGER = reg("shadow_dagger", "Shadow Dagger", CustomRarity.LEGENDARY, CustomItemCategory.SPECIAL, ElementRegistry.SHADOW, 8.5, 2.4, 1650, MobEffects.INVISIBILITY,
            "Umbral Cut", 60, "SLASH", 8.0f, "Phantom Step", 90, "TELEPORT", 8.5f, "Nightmare Wave", 120, "WAVE", 9.0f, "Shadow Execution", 440, "ULTIMATE_CATACLYSM", 17.0f);

    // ==================== AXES ====================
    public static final CustomItem INFERNO_AXE = reg("inferno_axe", "Inferno Axe", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 11.5, 1.1, 2100, MobEffects.FIRE_RESISTANCE,
            "Blazing Cleave", 90, "SLASH", 10.5f, "Flame Charge", 110, "DASH", 9.0f, "Scorching Slam", 140, "SLAM", 11.0f, "Hellfire Storm", 500, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem FROST_AXE = reg("frost_axe", "Frost Axe", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.ICE, 11.0, 1.1, 2000, null,
            "Permafrost Rend", 90, "SLASH", 10.0f, "Glacier Slam", 120, "SLAM", 10.0f, "Frost Nova", 140, "WAVE", 9.5f, "Ice Age", 480, "ULTIMATE_CATACLYSM", 18.0f);

    public static final CustomItem THUNDER_AXE = reg("thunder_axe", "Thunder Axe", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.LIGHTNING, 11.5, 1.15, 2100, null,
            "Thunder Slash", 80, "SLASH", 10.0f, "Lightning Dash", 100, "DASH", 9.0f, "Lightning Shockwave", 140, "WAVE", 10.5f, "Thunderstorm", 500, "ULTIMATE_CATACLYSM", 19.5f);

    public static final CustomItem VOID_AXE = reg("void_axe", "Void Axe", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.VOID, 12.0, 1.1, 2300, null,
            "Rift Cleave", 90, "SLASH", 11.0f, "Void Pull", 120, "VORTEX", 10.0f, "Dimensional Blink", 150, "TELEPORT", 10.5f, "Void Annihilation", 520, "ULTIMATE_CATACLYSM", 20.5f);

    public static final CustomItem EARTH_AXE = reg("earth_axe", "Earth Axe", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.EARTH, 11.5, 1.0, 2200, MobEffects.RESISTANCE,
            "Boulder Cleave", 90, "SLASH", 10.0f, "Tectonic Slam", 120, "SLAM", 10.5f, "Stone Bastion", 160, "SHIELD_BASTION", 8.0f, "Continental Crush", 480, "ULTIMATE_CATACLYSM", 18.5f);

    // ==================== MACES & HAMMERS ====================
    public static final CustomItem THUNDER_MACE = reg("thunder_mace", "Thunder Mace", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.LIGHTNING, 12.0, 1.0, 2200, null,
            "Voltaic Smash", 90, "SLAM", 11.0f, "Storm Lunge", 110, "DASH", 9.5f, "Shockwave", 140, "WAVE", 10.5f, "Mjolnir Judgment", 520, "ULTIMATE_CATACLYSM", 20.0f);

    public static final CustomItem EARTH_MACE = reg("earth_mace", "Earth Mace", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.EARTH, 12.5, 0.95, 2400, MobEffects.RESISTANCE,
            "Earth Slam", 90, "SLAM", 11.5f, "Stone Charge", 110, "DASH", 9.5f, "Ground Shockwave", 140, "WAVE", 10.5f, "Earthquake Ultimate", 520, "ULTIMATE_CATACLYSM", 20.0f);

    public static final CustomItem VOID_MACE = reg("void_mace", "Void Mace", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.VOID, 13.0, 0.95, 2500, null,
            "Singularity Smash", 90, "SLAM", 12.0f, "Gravity Crush", 120, "VORTEX", 10.5f, "Void Rift", 150, "TELEPORT", 11.0f, "Black Hole Collapse", 540, "ULTIMATE_CATACLYSM", 21.5f);

    public static final CustomItem INFERNO_MACE = reg("inferno_mace", "Inferno Mace", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 12.0, 1.0, 2100, MobEffects.FIRE_RESISTANCE,
            "Meteor Smash", 90, "SLAM", 11.0f, "Blaze Charge", 110, "DASH", 9.0f, "Eruption Wave", 140, "WAVE", 10.5f, "Core Meltdown", 500, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem CELESTIAL_MACE = reg("celestial_mace", "Celestial Mace", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.LIGHT, 13.0, 1.05, 2600, MobEffects.REGENERATION,
            "Starlight Smash", 90, "SLAM", 11.5f, "Holy Bastion", 130, "SHIELD_BASTION", 9.0f, "Radiant Wave", 140, "WAVE", 11.0f, "Heavens Fall", 540, "ULTIMATE_CATACLYSM", 21.0f);

    public static final CustomItem EARTH_HAMMER = reg("earth_hammer", "Earth Hammer", CustomRarity.MYTHIC, CustomItemCategory.TOOLS, ElementRegistry.EARTH, 13.5, 0.85, 2800, MobEffects.RESISTANCE,
            "Seismic Shatter", 100, "SLAM", 12.5f, "Bulwark Charge", 120, "DASH", 10.0f, "Tremor Ring", 150, "WAVE", 11.5f, "Earthquake", 540, "ULTIMATE_CATACLYSM", 22.0f);

    public static final CustomItem GRAVITY_HAMMER = reg("gravity_hammer", "Gravity Hammer", CustomRarity.ANCIENT, CustomItemCategory.SPECIAL, ElementRegistry.VOID, 14.0, 0.85, 2900, null,
            "Zero-G Slam", 100, "SLAM", 13.0f, "Gravity Well", 130, "VORTEX", 11.5f, "Repulsor Wave", 150, "WAVE", 12.0f, "Gravitational Collapse", 560, "ULTIMATE_CATACLYSM", 23.0f);

    // ==================== SPEARS ====================
    public static final CustomItem THUNDER_SPEAR = reg("thunder_spear", "Thunder Spear", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.LIGHTNING, 10.5, 1.45, 2100, null,
            "Lightning Thrust", 70, "SLASH", 9.5f, "Thunder Javelin", 110, "SPEAR_THROW", 11.5f, "Storm Dash", 120, "DASH", 9.5f, "Thunder God Strike", 500, "ULTIMATE_CATACLYSM", 20.0f);

    public static final CustomItem OCEAN_SPEAR = reg("ocean_spear", "Ocean Spear", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.WATER, 10.0, 1.45, 2000, MobEffects.CONDUIT_POWER,
            "Tidal Thrust", 70, "SLASH", 9.0f, "Water Spear Throw", 110, "SPEAR_THROW", 10.5f, "Riptide Dash", 110, "DASH", 9.5f, "Maelstrom Deluge", 480, "ULTIMATE_CATACLYSM", 18.5f);

    public static final CustomItem FLAME_SPEAR = reg("flame_spear", "Flame Spear", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 10.0, 1.4, 1950, MobEffects.FIRE_RESISTANCE,
            "Searing Thrust", 70, "SLASH", 9.0f, "Blazing Javelin", 110, "SPEAR_THROW", 11.0f, "Flare Dash", 110, "DASH", 9.0f, "Solar Lance", 480, "ULTIMATE_CATACLYSM", 18.5f);

    public static final CustomItem FROST_SPEAR = reg("frost_spear", "Frost Spear", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.ICE, 10.0, 1.45, 2000, null,
            "Icicle Thrust", 70, "SLASH", 9.0f, "Ice Lance Throw", 110, "SPEAR_THROW", 11.0f, "Frost Nova", 130, "WAVE", 9.5f, "Absolute Zero", 500, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem VOID_SPEAR = reg("void_spear", "Void Spear", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.VOID, 11.0, 1.45, 2200, null,
            "Rift Thrust", 70, "SLASH", 10.0f, "Void Javelin", 110, "SPEAR_THROW", 12.0f, "Phase Blink", 140, "TELEPORT", 10.0f, "Dimension Piercer", 520, "ULTIMATE_CATACLYSM", 20.5f);

    public static final CustomItem WIND_SPEAR = reg("wind_spear", "Wind Spear", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.WIND, 9.5, 1.65, 1850, MobEffects.SPEED,
            "Zephyr Thrust", 60, "SLASH", 8.5f, "Sonic Javelin", 100, "SPEAR_THROW", 10.0f, "Sky Leap", 90, "DASH", 8.5f, "Hurricane Vortex", 460, "ULTIMATE_CATACLYSM", 17.5f);

    public static final CustomItem ANCIENT_SPEAR = reg("ancient_spear", "Ancient Spear", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.EARTH, 11.5, 1.5, 2600, MobEffects.STRENGTH,
            "Relic Thrust", 70, "SLASH", 10.5f, "Titan Javelin", 110, "SPEAR_THROW", 12.5f, "Earth Tremor", 130, "SLAM", 11.0f, "Spear of Ages", 540, "ULTIMATE_CATACLYSM", 21.5f);

    // ==================== SCYTHES ====================
    public static final CustomItem WIND_SCYTHE = reg("wind_scythe", "Wind Scythe", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.WIND, 10.5, 1.5, 2000, MobEffects.SPEED,
            "Reaping Gale", 75, "SLASH", 9.5f, "Tornado Pull", 110, "VORTEX", 9.5f, "Wind Step", 95, "DASH", 8.5f, "Tempest Harvest", 480, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem SOUL_SCYTHE = reg("soul_scythe", "Soul Scythe", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.SHADOW, 11.5, 1.45, 2300, MobEffects.NIGHT_VISION,
            "Soul Reap", 80, "SLASH", 10.5f, "Wraith Vortex", 120, "VORTEX", 10.0f, "Soul Blink", 140, "TELEPORT", 10.0f, "Grim Harvest", 520, "ULTIMATE_CATACLYSM", 20.5f);

    // ==================== BOWS ====================
    public static final CustomItem STORM_BOW = reg("storm_bow", "Storm Bow", CustomRarity.LEGENDARY, CustomItemCategory.WEAPONS, ElementRegistry.LIGHTNING, 7.0, 1.4, 1800, null,
            "Lightning Arrow", 70, "SPEAR_THROW", 10.5f, "Volley Shot", 80, "ARROW_BARRAGE", 9.0f, "Storm Burst", 130, "WAVE", 10.0f, "Thunderstorm Ultimate", 480, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem INFERNO_BOW = reg("inferno_bow", "Inferno Bow", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.FIRE, 7.0, 1.4, 1750, MobEffects.FIRE_RESISTANCE,
            "Phoenix Arrow", 70, "PROJECTILE", 10.0f, "Flame Volley", 80, "ARROW_BARRAGE", 9.5f, "Backdraft Dash", 110, "DASH", 8.0f, "Meteor Shower", 480, "ULTIMATE_CATACLYSM", 18.5f);

    public static final CustomItem FROST_BOW = reg("frost_bow", "Frost Bow", CustomRarity.EPIC, CustomItemCategory.WEAPONS, ElementRegistry.ICE, 6.5, 1.45, 1750, null,
            "Cryo Arrow", 70, "PROJECTILE", 9.5f, "Icicle Volley", 80, "ARROW_BARRAGE", 9.0f, "Frost Ring", 120, "WAVE", 9.5f, "Hailstorm", 480, "ULTIMATE_CATACLYSM", 18.0f);

    public static final CustomItem VOID_BOW = reg("void_bow", "Void Bow", CustomRarity.MYTHIC, CustomItemCategory.WEAPONS, ElementRegistry.VOID, 8.0, 1.45, 2000, null,
            "Void Piercer", 75, "SPEAR_THROW", 11.5f, "Dark Volley", 85, "ARROW_BARRAGE", 10.0f, "Rift Step", 130, "TELEPORT", 9.5f, "Oblivion Rain", 520, "ULTIMATE_CATACLYSM", 20.0f);

    public static final CustomItem WIND_BOW = reg("wind_bow", "Wind Bow", CustomRarity.RARE, CustomItemCategory.WEAPONS, ElementRegistry.WIND, 6.5, 1.7, 1650, MobEffects.SPEED,
            "Gale Shot", 60, "PROJECTILE", 8.5f, "Rapid Volley", 70, "ARROW_BARRAGE", 8.5f, "Tailwind Leap", 90, "DASH", 8.0f, "Eye of the Storm", 440, "ULTIMATE_CATACLYSM", 17.0f);

    public static final CustomItem TEMPEST_BOW = reg("tempest_bow", "Tempest Bow", CustomRarity.ANCIENT, CustomItemCategory.WEAPONS, ElementRegistry.LIGHTNING, 8.5, 1.6, 2400, MobEffects.SPEED,
            "Tempest Bolt", 70, "SPEAR_THROW", 12.0f, "Cyclone Volley", 80, "ARROW_BARRAGE", 10.5f, "Hurricane Vortex", 130, "VORTEX", 10.5f, "Wrath of the Skies", 540, "ULTIMATE_CATACLYSM", 21.5f);

    // ==================== STAFFS & WANDS ====================
    public static final CustomItem THUNDER_STAFF = reg("thunder_staff", "Thunder Staff", CustomRarity.LEGENDARY, CustomItemCategory.MAGIC, ElementRegistry.LIGHTNING, 7.5, 1.3, 1800, null,
            "Arc Lash", 70, "SLASH", 9.0f, "Lightning Strike", 110, "SPEAR_THROW", 11.5f, "Static Field", 140, "WAVE", 10.0f, "Heaven's Judgment", 500, "ULTIMATE_CATACLYSM", 19.5f);

    public static final CustomItem LIGHT_STAFF = reg("light_staff", "Light Staff", CustomRarity.LEGENDARY, CustomItemCategory.MAGIC, ElementRegistry.LIGHT, 7.0, 1.35, 1850, MobEffects.REGENERATION,
            "Radiant Arc", 70, "SLASH", 8.5f, "Light Beam", 100, "PROJECTILE", 10.0f, "Holy Shield", 150, "SHIELD_BASTION", 8.5f, "Light Burst", 480, "ULTIMATE_CATACLYSM", 18.5f);

    public static final CustomItem WATER_STAFF = reg("water_staff", "Water Staff", CustomRarity.LEGENDARY, CustomItemCategory.MAGIC, ElementRegistry.WATER, 7.5, 1.35, 1850, MobEffects.CONDUIT_POWER,
            "Water Whip", 70, "SLASH", 8.5f, "Water Blast", 90, "PROJECTILE", 10.0f, "Water Wave", 130, "WAVE", 10.5f, "Ocean Burst", 480, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem VOID_STAFF = reg("void_staff", "Void Staff", CustomRarity.ANCIENT, CustomItemCategory.MAGIC, ElementRegistry.VOID, 8.5, 1.3, 2200, null,
            "Dark Matter Lash", 75, "SLASH", 10.0f, "Void Blast", 100, "PROJECTILE", 11.5f, "Void Rift", 140, "VORTEX", 11.0f, "Void Collapse", 540, "ULTIMATE_CATACLYSM", 22.0f);

    public static final CustomItem METEOR_STAFF = reg("meteor_staff", "Meteor Staff", CustomRarity.MYTHIC, CustomItemCategory.MAGIC, ElementRegistry.FIRE, 8.0, 1.25, 2000, MobEffects.FIRE_RESISTANCE,
            "Ember Sweep", 75, "SLASH", 9.5f, "Fireball Launch", 95, "PROJECTILE", 11.0f, "Magma Crater", 140, "SLAM", 11.5f, "Meteor Flame", 520, "ULTIMATE_CATACLYSM", 21.0f);

    public static final CustomItem CRYSTAL_STAFF = reg("crystal_staff", "Crystal Staff", CustomRarity.EPIC, CustomItemCategory.MAGIC, ElementRegistry.ICE, 7.5, 1.35, 1800, null,
            "Prism Slash", 70, "SLASH", 8.5f, "Crystal Shard", 95, "PROJECTILE", 10.0f, "Crystal Bastion", 150, "SHIELD_BASTION", 8.0f, "Shattered Aurora", 480, "ULTIMATE_CATACLYSM", 18.0f);

    public static final CustomItem NETHER_STAFF = reg("nether_staff", "Nether Staff", CustomRarity.LEGENDARY, CustomItemCategory.MAGIC, ElementRegistry.FIRE, 8.0, 1.3, 1950, MobEffects.FIRE_RESISTANCE,
            "Hellfire Lash", 75, "SLASH", 9.5f, "Soul Fireball", 95, "PROJECTILE", 10.5f, "Nether Wave", 135, "WAVE", 10.5f, "Infernal Dominion", 500, "ULTIMATE_CATACLYSM", 19.5f);

    public static final CustomItem ARCANE_WAND = reg("arcane_wand", "Arcane Wand", CustomRarity.RARE, CustomItemCategory.MAGIC, ElementRegistry.SHADOW, 6.5, 1.6, 1500, null,
            "Arcane Bolt", 60, "PROJECTILE", 8.5f, "Blink Step", 100, "TELEPORT", 8.5f, "Mana Pulse", 120, "WAVE", 9.0f, "Arcane Overload", 440, "ULTIMATE_CATACLYSM", 17.0f);

    // ==================== SHIELDS, ORBS & UTILITY ====================
    public static final CustomItem GUARDIAN_SHIELD = reg("guardian_shield", "Guardian Shield", CustomRarity.LEGENDARY, CustomItemCategory.SHIELDS, ElementRegistry.EARTH, 6.0, 1.1, 2800, MobEffects.RESISTANCE,
            "Shield Bash", 70, "SLAM", 8.5f, "Guardian Barrier", 160, "SHIELD_BASTION", 8.0f, "Tremor Push", 130, "WAVE", 9.0f, "Immortal Fortress", 500, "ULTIMATE_CATACLYSM", 17.5f);

    public static final CustomItem ELEMENTAL_SHIELD = reg("elemental_shield", "Elemental Shield", CustomRarity.MYTHIC, CustomItemCategory.SHIELDS, ElementRegistry.LIGHT, 6.5, 1.15, 3000, MobEffects.RESISTANCE,
            "Aegis Strike", 70, "SLAM", 9.0f, "Elemental Aegis", 160, "SHIELD_BASTION", 9.0f, "Prism Pulse", 130, "WAVE", 10.0f, "Sanctuary Nova", 520, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem VOID_ORB = reg("void_orb", "Void Orb", CustomRarity.MYTHIC, CustomItemCategory.ELEMENTS, ElementRegistry.VOID, 6.0, 1.5, 1600, null,
            "Void Lash", 70, "SLASH", 9.0f, "Void Pulse", 120, "VORTEX", 10.5f, "Phase Shift", 140, "TELEPORT", 9.5f, "Void Apocalypse", 520, "ULTIMATE_CATACLYSM", 21.0f);

    public static final CustomItem DARK_ORB = reg("dark_orb", "Dark Orb", CustomRarity.LEGENDARY, CustomItemCategory.ELEMENTS, ElementRegistry.SHADOW, 6.0, 1.5, 1550, MobEffects.NIGHT_VISION,
            "Shadow Bolt", 70, "PROJECTILE", 9.5f, "Darkness Field", 120, "VORTEX", 9.5f, "Umbral Wave", 135, "WAVE", 10.0f, "Total Eclipse", 500, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem LIGHT_ORB = reg("light_orb", "Light Orb", CustomRarity.LEGENDARY, CustomItemCategory.ELEMENTS, ElementRegistry.LIGHT, 6.0, 1.5, 1550, MobEffects.REGENERATION,
            "Photon Ray", 70, "PROJECTILE", 9.5f, "Healing Aura", 140, "SHIELD_BASTION", 8.5f, "Solar Pulse", 130, "WAVE", 10.0f, "Dawnbringer", 500, "ULTIMATE_CATACLYSM", 19.0f);

    public static final CustomItem GRAPPLING_HOOK = reg("grappling_hook", "Grappling Hook", CustomRarity.UNCOMMON, CustomItemCategory.UTILITY, ElementRegistry.WIND, 5.0, 1.8, 1200, MobEffects.SPEED,
            "Hook Slash", 60, "SLASH", 7.0f, "Grapple Pull", 65, "GRAPPLE", 6.0f, " Gale Boost", 90, "DASH", 7.5f, "Skyline Overdrive", 360, "ULTIMATE_CATACLYSM", 14.5f);

    private static CustomItem reg(
            String id, String name, CustomRarity rarity, CustomItemCategory category,
            CustomElement element, double dmg, double spd, int durability, Holder<MobEffect> passive,
            String slName, int slCd, String slType, float slPow,
            String rName, int rCd, String rType, float rPow,
            String srName, int srCd, String srType, float srPow,
            String ultName, int ultCd, String ultType, float ultPow
    ) {
        return register(new CustomItemData(
                id, name, rarity, category, element, dmg, spd, durability, 1, passive,
                AbilityRegistry.createWeaponKit(id, element, slName, slCd, slType, slPow, rName, rCd, rType, rPow, srName, srCd, srType, srPow, ultName, ultCd, ultType, ultPow)
        ));
    }

    public static CustomItem register(CustomItemData data) {
        if (!AssetValidator.validateItemData(data)) {
            throw new IllegalArgumentException("Invalid CustomItemData: " + (data != null ? data.id() : "null"));
        }
        if (ITEMS.containsKey(data.id())) {
            return ITEMS.get(data.id());
        }
        Identifier id = CustomArsenalMod.id(data.id());
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item.Properties props = new Item.Properties().setId(key);
        CustomItem item = new CustomItem(data, props);
        Registry.register(BuiltInRegistries.ITEM, key, item);
        ITEMS.put(data.id(), item);
        return item;
    }

    public static CustomItem get(String id) {
        return ITEMS.get(id);
    }

    public static Collection<CustomItem> getAll() {
        return Collections.unmodifiableCollection(ITEMS.values());
    }

    public static int size() {
        return ITEMS.size();
    }

    public static void init() {
    }
}
