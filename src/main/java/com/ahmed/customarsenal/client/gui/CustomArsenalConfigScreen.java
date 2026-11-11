package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.ability.AbilityRegistry;
import com.ahmed.customarsenal.ability.ActivationHand;
import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.config.CustomItemConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CustomArsenalConfigScreen extends Screen {
    private final Screen parent;
    private int abilityPageOffset = 0;

    public CustomArsenalConfigScreen(Screen parent) {
        super(Component.literal("Custom Arsenal Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        CustomItemConfig cfg = CustomItemConfig.get();
        int centerX = this.width / 2;
        int topY = 32;

        this.addRenderableWidget(Button.builder(
                Component.literal("Custom Particles: " + (cfg.enableCustomParticles ? "ON" : "OFF")),
                btn -> {
                    cfg.enableCustomParticles = !cfg.enableCustomParticles;
                    btn.setMessage(Component.literal("Custom Particles: " + (cfg.enableCustomParticles ? "ON" : "OFF")));
                }).bounds(centerX - 155, topY, 150, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Particle Intensity: " + Math.round(cfg.particleIntensity * 100) + "%"),
                btn -> {
                    cfg.particleIntensity = cfg.particleIntensity >= 2.0f ? 0.25f : cfg.particleIntensity + 0.25f;
                    btn.setMessage(Component.literal("Particle Intensity: " + Math.round(cfg.particleIntensity * 100) + "%"));
                }).bounds(centerX + 5, topY, 150, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Custom Sounds: " + (cfg.enableCustomSounds ? "ON" : "OFF")),
                btn -> {
                    cfg.enableCustomSounds = !cfg.enableCustomSounds;
                    btn.setMessage(Component.literal("Custom Sounds: " + (cfg.enableCustomSounds ? "ON" : "OFF")));
                }).bounds(centerX - 155, topY + 24, 150, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Cooldown HUD: " + (cfg.showCooldownIndicators ? "ON" : "OFF")),
                btn -> {
                    cfg.showCooldownIndicators = !cfg.showCooldownIndicators;
                    btn.setMessage(Component.literal("Cooldown HUD: " + (cfg.showCooldownIndicators ? "ON" : "OFF")));
                }).bounds(centerX + 5, topY + 24, 150, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Ability Animations: " + (cfg.enableAbilityAnimations ? "ON" : "OFF")),
                btn -> {
                    cfg.enableAbilityAnimations = !cfg.enableAbilityAnimations;
                    btn.setMessage(Component.literal("Ability Animations: " + (cfg.enableAbilityAnimations ? "ON" : "OFF")));
                }).bounds(centerX - 155, topY + 48, 150, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("3D Model Rendering: " + (cfg.enableCustom3DRendering ? "ON" : "OFF")),
                btn -> {
                    cfg.enableCustom3DRendering = !cfg.enableCustom3DRendering;
                    btn.setMessage(Component.literal("3D Model Rendering: " + (cfg.enableCustom3DRendering ? "ON" : "OFF")));
                }).bounds(centerX + 5, topY + 48, 150, 20).build());

        List<CustomAbility> abilities = new ArrayList<>(AbilityRegistry.getAll());
        int startIdx = abilityPageOffset * 4;
        for (int i = 0; i < 4 && (startIdx + i) < abilities.size(); i++) {
            CustomAbility ability = abilities.get(startIdx + i);
            int rowY = topY + 88 + (i * 22);

            this.addRenderableWidget(Button.builder(
                    Component.literal(ability.displayName() + " Hand: " + ability.getEffectiveHand().getDisplayName()),
                    btn -> {
                        ActivationHand next = ability.getEffectiveHand().next();
                        cfg.activationHandOverrides.put(ability.id(), next.name());
                        btn.setMessage(Component.literal(ability.displayName() + " Hand: " + next.getDisplayName()));
                    }).bounds(centerX - 155, rowY, 215, 20).build());

            this.addRenderableWidget(Button.builder(
                    Component.literal(cfg.isAbilityEnabled(ability.id()) ? "Enabled" : "Disabled"),
                    btn -> {
                        boolean next = !cfg.isAbilityEnabled(ability.id());
                        cfg.abilityEnabledStates.put(ability.id(), next);
                        btn.setMessage(Component.literal(next ? "Enabled" : "Disabled"));
                    }).bounds(centerX + 65, rowY, 90, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("< Prev Abilities"), btn -> {
            if (abilityPageOffset > 0) {
                abilityPageOffset--;
                rebuildWidgets();
            }
        }).bounds(centerX - 155, this.height - 52, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Next Abilities >"), btn -> {
            if ((abilityPageOffset + 1) * 4 < abilities.size()) {
                abilityPageOffset++;
                rebuildWidgets();
            }
        }).bounds(centerX + 55, this.height - 52, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save & Done"), btn -> {
            CustomItemConfig.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(centerX - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, "Contextual Ability Hand & Toggle Settings (Keybinds in Options -> Controls -> Custom Arsenal)",
                this.width / 2, 106, 0xFFAAAAAA);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        CustomItemConfig.save();
        this.minecraft.setScreen(this.parent);
    }
}
