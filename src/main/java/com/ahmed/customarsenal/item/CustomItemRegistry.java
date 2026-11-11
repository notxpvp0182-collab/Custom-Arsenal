package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.ability.AbilityRegistry;
import com.ahmed.customarsenal.element.ElementRegistry;
import com.ahmed.customarsenal.util.AssetValidator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.*;

public final class CustomItemRegistry {
    private static final Map<String, CustomItem> ITEMS = new LinkedHashMap<>();

    public static final CustomItem SHADOW_SWORD = register(new CustomItemData(
            "shadow_sword",
            "Shadow Sword",
            List.of("Forged in the Umbral Abyss.", "Channels multi-stage shadow techniques."),
            Rarity.EPIC,
            CustomItemCategory.WEAPONS,
            ElementRegistry.SHADOW,
            9.5, 1.6, 1850, 1,
            MobEffects.NIGHT_VISION,
            List.of(AbilityRegistry.SHADOW_DASH, AbilityRegistry.SHADOW_STRIKE, AbilityRegistry.SHADOW_TELEPORT)
    ));

    public static final CustomItem INFERNO_SWORD = register(new CustomItemData(
            "inferno_sword",
            "Inferno Sword",
            List.of("A blazing broadsword with a molten core.", "Ignites targets on every strike."),
            Rarity.RARE,
            CustomItemCategory.WEAPONS,
            ElementRegistry.FIRE,
            9.0, 1.5, 1600, 1,
            MobEffects.FIRE_RESISTANCE,
            List.of(AbilityRegistry.FIRE_SLASH)
    ));

    public static final CustomItem FROST_BLADE = register(new CustomItemData(
            "frost_blade",
            "Frost Blade",
            List.of("Carved from eternal permafrost.", "Chills enemies to the bone."),
            Rarity.RARE,
            CustomItemCategory.WEAPONS,
            ElementRegistry.ICE,
            8.5, 1.65, 1550, 1,
            null,
            List.of(AbilityRegistry.FROST_WAVE)
    ));

    public static final CustomItem THUNDER_STAFF = register(new CustomItemData(
            "thunder_staff",
            "Thunder Staff",
            List.of("Conducts atmospheric fury through a storm-crystal spire."),
            Rarity.EPIC,
            CustomItemCategory.MAGIC,
            ElementRegistry.LIGHTNING,
            6.5, 1.2, 1200, 1,
            null,
            List.of(AbilityRegistry.LIGHTNING_STRIKE)
    ));

    public static final CustomItem VOID_ORB = register(new CustomItemData(
            "void_orb",
            "Void Orb",
            List.of("A singularity contained within dark glass.", "Warps gravity upon command."),
            Rarity.EPIC,
            CustomItemCategory.ELEMENTS,
            ElementRegistry.VOID,
            5.0, 1.4, 900, 1,
            null,
            List.of(AbilityRegistry.VOID_PULSE)
    ));

    public static final CustomItem GUARDIAN_SHIELD = register(new CustomItemData(
            "guardian_shield",
            "Guardian Shield",
            List.of("An immovable bulwark imbued with ancient earth runes."),
            Rarity.RARE,
            CustomItemCategory.SHIELDS,
            ElementRegistry.EARTH,
            4.5, 1.0, 2200, 1,
            MobEffects.RESISTANCE,
            List.of(AbilityRegistry.GUARDIAN_BARRIER)
    ));

    public static final CustomItem GRAPPLING_HOOK = register(new CustomItemData(
            "grappling_hook",
            "Grappling Hook",
            List.of("Mechanical claw launcher for rapid vertical & horizontal traversal."),
            Rarity.UNCOMMON,
            CustomItemCategory.UTILITY,
            ElementRegistry.WIND,
            3.5, 1.8, 800, 1,
            null,
            List.of(AbilityRegistry.GRAPPLE)
    ));

    public static final CustomItem WIND_BLADE = register(new CustomItemData(
            "wind_blade",
            "Wind Blade",
            List.of("Featherweight katana that slices the air itself."),
            Rarity.RARE,
            CustomItemCategory.WEAPONS,
            ElementRegistry.WIND,
            7.5, 2.0, 1400, 1,
            MobEffects.SPEED,
            List.of(AbilityRegistry.WIND_DASH)
    ));

    public static final CustomItem POISON_DAGGER = register(new CustomItemData(
            "poison_dagger",
            "Poison Dagger",
            List.of("Curved assassin's stiletto coated in virulent neurotoxin."),
            Rarity.RARE,
            CustomItemCategory.SPECIAL,
            ElementRegistry.POISON,
            7.0, 2.2, 1100, 1,
            null,
            List.of(AbilityRegistry.POISON_STRIKE)
    ));

    public static final CustomItem LIGHT_STAFF = register(new CustomItemData(
            "light_staff",
            "Light Staff",
            List.of("Radiant scepter of dawn that restores allies and purges darkness."),
            Rarity.EPIC,
            CustomItemCategory.MAGIC,
            ElementRegistry.LIGHT,
            6.0, 1.3, 1350, 1,
            null,
            List.of(AbilityRegistry.LIGHT_BURST)
    ));

    public static CustomItem register(CustomItemData data) {
        if (!AssetValidator.validateItemData(data)) {
            throw new IllegalArgumentException("Invalid CustomItemData: " + (data != null ? data.id() : "null"));
        }
        if (ITEMS.containsKey(data.id())) {
            CustomArsenalMod.LOGGER.error("Duplicate CustomItem ID '{}' prevented!", data.id());
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
