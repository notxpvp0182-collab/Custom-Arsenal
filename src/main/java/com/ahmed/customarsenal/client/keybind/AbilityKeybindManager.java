package com.ahmed.customarsenal.client.keybind;

import com.ahmed.customarsenal.ability.AbilityCooldownManager;
import com.ahmed.customarsenal.ability.AbilityManager;
import com.ahmed.customarsenal.client.gui.CustomInventoryScreen;
import com.ahmed.customarsenal.config.CustomItemConfig;
import com.ahmed.customarsenal.network.ActivateAbilityC2SPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

/**
 * Does NOT register keys into Minecraft's vanilla Controls > Keybinds list.
 * Instead, keys are configured exclusively inside Mod Menu -> Custom Arsenal -> Configure -> KEYBINDS.
 */
public final class AbilityKeybindManager {
    private static boolean prevUniversalDown = false;
    private static boolean prevSecondaryDown = false;
    private static boolean prevUtilityDown = false;
    private static boolean prevInventoryDown = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(AbilityKeybindManager::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) {
            prevUniversalDown = false;
            prevSecondaryDown = false;
            prevUtilityDown = false;
            prevInventoryDown = false;
            return;
        }

        CustomItemConfig cfg = CustomItemConfig.get();
        if (!cfg.enableCustomArsenal) return;

        long window = client.getWindow().getWindow();

        boolean univDown = isKeyPressed(window, cfg.universalAbilityKey);
        if (univDown && !prevUniversalDown) {
            handleContextualKeyPress(client, "slot_1");
        }
        prevUniversalDown = univDown;

        boolean secDown = isKeyPressed(window, cfg.secondaryAbilityKey);
        if (secDown && !prevSecondaryDown) {
            handleContextualKeyPress(client, "slot_2");
        }
        prevSecondaryDown = secDown;

        boolean utilDown = isKeyPressed(window, cfg.utilityAbilityKey);
        if (utilDown && !prevUtilityDown) {
            handleContextualKeyPress(client, "slot_3");
        }
        prevUtilityDown = utilDown;

        boolean invDown = isKeyPressed(window, cfg.openInventoryKey);
        if (invDown && !prevInventoryDown && client.player.isCreative()) {
            client.setScreen(new CustomInventoryScreen(null));
        }
        prevInventoryDown = invDown;
    }

    private static boolean isKeyPressed(long window, int glfwKey) {
        if (glfwKey <= 0) return false;
        return GLFW.glfwGetKey(window, glfwKey) == GLFW.GLFW_PRESS;
    }

    private static void handleContextualKeyPress(Minecraft client, String keySlot) {
        Optional<AbilityManager.ResolvedAbility> resolvedOpt = AbilityManager.resolveContextualAbility(client.player, keySlot);
        if (resolvedOpt.isEmpty()) {
            return; // Do nothing if no compatible Custom Arsenal item is held in the required hand
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
        CustomItemConfig cfg = CustomItemConfig.get();
        return switch (keySlot) {
            case "slot_1" -> formatKey(cfg.universalAbilityKey);
            case "slot_2" -> formatKey(cfg.secondaryAbilityKey);
            case "slot_3" -> formatKey(cfg.utilityAbilityKey);
            case "shift_left" -> "S+LMB";
            case "right_click" -> "RMB";
            case "shift_right" -> "S+RMB";
            default -> formatKey(cfg.universalAbilityKey);
        };
    }

    public static String formatKey(int keyCode) {
        if (keyCode <= 0) return "NONE";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isBlank()) {
            return name.toUpperCase();
        }
        return InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString().toUpperCase();
    }
}
