package com.ahmed.customarsenal.client.keybind;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.ability.AbilityCooldownManager;
import com.ahmed.customarsenal.ability.AbilityManager;
import com.ahmed.customarsenal.client.gui.CustomInventoryScreen;
import com.ahmed.customarsenal.network.ActivateAbilityC2SPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public final class AbilityKeybindManager {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(CustomArsenalMod.id("keys"));

    public static KeyMapping ABILITY_SLOT_1;
    public static KeyMapping ABILITY_SLOT_2;
    public static KeyMapping ABILITY_SLOT_3;
    public static KeyMapping SHIELD_ABILITY;
    public static KeyMapping OPEN_CUSTOM_INVENTORY;

    public static void init() {
        ABILITY_SLOT_1 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.custom_arsenal.ability_1",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CATEGORY
        ));
        ABILITY_SLOT_2 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.custom_arsenal.ability_2",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        ));
        ABILITY_SLOT_3 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.custom_arsenal.ability_3",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));
        SHIELD_ABILITY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.custom_arsenal.shield_ability",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));
        OPEN_CUSTOM_INVENTORY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.custom_arsenal.open_inventory",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(AbilityKeybindManager::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) {
            return;
        }

        while (OPEN_CUSTOM_INVENTORY.consumeClick()) {
            if (client.player.isCreative()) {
                client.setScreen(new CustomInventoryScreen(null));
            }
        }

        while (ABILITY_SLOT_1.consumeClick()) {
            handleContextualKeyPress(client, "slot_1");
        }
        while (ABILITY_SLOT_2.consumeClick()) {
            handleContextualKeyPress(client, "slot_2");
        }
        while (ABILITY_SLOT_3.consumeClick()) {
            handleContextualKeyPress(client, "slot_3");
        }
        while (SHIELD_ABILITY.consumeClick()) {
            handleContextualKeyPress(client, "shield");
        }
    }

    private static void handleContextualKeyPress(Minecraft client, String keySlot) {
        Optional<AbilityManager.ResolvedAbility> resolvedOpt = AbilityManager.resolveContextualAbility(client.player, keySlot);
        if (resolvedOpt.isEmpty()) {
            return;
        }
        AbilityManager.ResolvedAbility resolved = resolvedOpt.get();
        long now = client.level.getGameTime();
        if (AbilityCooldownManager.isClientOnCooldown(resolved.ability().id(), now)) {
            return;
        }
        ClientPlayNetworking.send(new ActivateAbilityC2SPayload(
                resolved.item().getCustomData().id(),
                resolved.ability().id(),
                resolved.hand() == InteractionHand.MAIN_HAND
        ));
    }

    public static String getBoundKeyName(String keySlot) {
        KeyMapping mapping = switch (keySlot) {
            case "slot_1" -> ABILITY_SLOT_1;
            case "slot_2" -> ABILITY_SLOT_2;
            case "slot_3" -> ABILITY_SLOT_3;
            case "shield" -> SHIELD_ABILITY;
            default -> ABILITY_SLOT_1;
        };
        return mapping != null ? mapping.getTranslatedKeyMessage().getString().toUpperCase() : "R";
    }
}
