package com.ahmed.customarsenal.client;

import com.ahmed.customarsenal.ability.AbilityCooldownManager;
import com.ahmed.customarsenal.client.gui.CustomHudOverlay;
import com.ahmed.customarsenal.client.gui.CustomInventoryScreen;
import com.ahmed.customarsenal.client.keybind.AbilityKeybindManager;
import com.ahmed.customarsenal.client.render.CustomItemRenderer;
import com.ahmed.customarsenal.client.render.CustomModelManager;
import com.ahmed.customarsenal.network.AbilityFeedbackS2CPayload;
import com.ahmed.customarsenal.network.CooldownSyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public class CustomArsenalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomModelManager.initClientAssets();
        AbilityKeybindManager.init();
        CustomItemRenderer.init();

        // Register S2C Receivers
        ClientPlayNetworking.registerGlobalReceiver(CooldownSyncS2CPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> {
                if (client.level != null) {
                    AbilityCooldownManager.setClientCooldown(
                            payload.abilityId(),
                            client.level.getGameTime(),
                            payload.cooldownTicks()
                    );
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AbilityFeedbackS2CPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> {
                InteractionHand hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                CustomItemRenderer.triggerActivationAnimation(hand);
            });
        });

        // Inject "Custom Items" Tab Button into Vanilla CreativeModeInventoryScreen
        // Preserves 100% of normal vanilla Creative Inventory tabs and functionality.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof CreativeModeInventoryScreen creativeScreen) {
                int guiLeft = (scaledWidth - 195) / 2;
                int guiTop = (scaledHeight - 136) / 2;
                Button customArsenalBtn = Button.builder(
                        Component.translatable("gui.custom_arsenal.tab_button"),
                        btn -> client.setScreen(new CustomInventoryScreen(creativeScreen))
                )
                .bounds(guiLeft + 198, guiTop + 4, 88, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.custom_arsenal.tab_tooltip")))
                .build();

                Screens.getButtons(creativeScreen).add(customArsenalBtn);
            }
        });

        // Register lightweight Cooldown & Contextual Ability HUD
        HudRenderCallback.EVENT.register(CustomHudOverlay::render);
    }
}
