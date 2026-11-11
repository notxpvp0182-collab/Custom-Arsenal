package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.client.keybind.AbilityKeybindManager;
import com.ahmed.customarsenal.config.CustomItemConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class CustomArsenalConfigScreen extends Screen {
    public enum Section {
        GENERAL("General"),
        ABILITY("Ability"),
        KEYBINDS("Keybinds"),
        PARTICLES("Particles"),
        RENDERING("Rendering"),
        PERFORMANCE("Performance");

        private final String label;
        Section(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final Screen parent;
    private Section activeSection = Section.GENERAL;
    private String listeningKeyTarget = null;

    private int panelW;
    private int panelH;
    private int panelX;
    private int panelY;

    public CustomArsenalConfigScreen(Screen parent) {
        super(Component.literal("Custom Arsenal Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.panelW = Mth.clamp(this.width - 24, 220, 340);
        this.panelH = Mth.clamp(this.height - 28, 165, 230);
        this.panelX = (this.width - this.panelW) / 2;
        this.panelY = (this.height - this.panelH) / 2;

        Section[] sections = Section.values();
        int tabW = (this.panelW - 12) / sections.length;
        for (int i = 0; i < sections.length; i++) {
            final Section sec = sections[i];
            int tx = this.panelX + 6 + (i * tabW);
            Button tabBtn = Button.builder(Component.literal(sec.getLabel()), btn -> {
                this.activeSection = sec;
                this.listeningKeyTarget = null;
                this.rebuildWidgets();
            }).bounds(tx, this.panelY + 20, tabW - 2, 16).build();
            tabBtn.active = (this.activeSection != sec);
            this.addRenderableWidget(tabBtn);
        }

        CustomItemConfig cfg = CustomItemConfig.get();
        int contentX = this.panelX + 14;
        int contentY = this.panelY + 44;
        int btnW = this.panelW - 28;
        int rowH = Math.min(22, (this.panelH - 76) / 4);

        switch (this.activeSection) {
            case GENERAL -> {
                addToggle(contentX, contentY, btnW, rowH - 2, "Custom Arsenal", cfg.enableCustomArsenal, v -> cfg.enableCustomArsenal = v);
                addToggle(contentX, contentY + rowH, btnW, rowH - 2, "Item Effects", cfg.enableCustomItemEffects, v -> cfg.enableCustomItemEffects = v);
                addToggle(contentX, contentY + rowH * 2, btnW, rowH - 2, "Custom Sounds", cfg.enableCustomSounds, v -> cfg.enableCustomSounds = v);
            }
            case ABILITY -> {
                addToggle(contentX, contentY, btnW, rowH - 2, "Cooldown Display", cfg.showCooldownIndicators, v -> cfg.showCooldownIndicators = v);
                addToggle(contentX, contentY + rowH, btnW, rowH - 2, "Ability Effects", cfg.enableAbilityVisualEffects, v -> cfg.enableAbilityVisualEffects = v);
                addToggle(contentX, contentY + rowH * 2, btnW, rowH - 2, "Ability Animations", cfg.enableAbilityAnimations, v -> cfg.enableAbilityAnimations = v);
            }
            case KEYBINDS -> {
                addKeybindButton(contentX, contentY, btnW, rowH - 2, "Universal Ability Key", "universal", cfg.universalAbilityKey);
                addKeybindButton(contentX, contentY + rowH, btnW, rowH - 2, "Secondary Ability Key", "secondary", cfg.secondaryAbilityKey);
                addKeybindButton(contentX, contentY + rowH * 2, btnW, rowH - 2, "Utility Ability Key", "utility", cfg.utilityAbilityKey);
                addKeybindButton(contentX, contentY + rowH * 3, btnW, rowH - 2, "Custom Inventory Key", "inventory", cfg.openInventoryKey);
            }
            case PARTICLES -> {
                addToggle(contentX, contentY, btnW, rowH - 2, "Particles", cfg.enableCustomParticles, v -> cfg.enableCustomParticles = v);
                this.addRenderableWidget(Button.builder(
                        Component.literal("Intensity: " + Math.round(cfg.particleIntensity * 100) + "%"),
                        btn -> {
                            cfg.particleIntensity = cfg.particleIntensity >= 2.0f ? 0.25f : cfg.particleIntensity + 0.25f;
                            btn.setMessage(Component.literal("Intensity: " + Math.round(cfg.particleIntensity * 100) + "%"));
                        }).bounds(contentX, contentY + rowH, btnW, rowH - 2).build());
                this.addRenderableWidget(Button.builder(
                        Component.literal("Density: " + Math.round(cfg.particleDensity * 100) + "%"),
                        btn -> {
                            cfg.particleDensity = cfg.particleDensity >= 2.0f ? 0.25f : cfg.particleDensity + 0.25f;
                            btn.setMessage(Component.literal("Density: " + Math.round(cfg.particleDensity * 100) + "%"));
                        }).bounds(contentX, contentY + rowH * 2, btnW, rowH - 2).build());
                addToggle(contentX, contentY + rowH * 3, btnW, rowH - 2, "Reduced Particles", cfg.reducedParticles, v -> cfg.reducedParticles = v);
            }
            case RENDERING -> {
                addToggle(contentX, contentY, btnW, rowH - 2, "Custom 3D Models", cfg.enableCustom3DRendering, v -> cfg.enableCustom3DRendering = v);
                addToggle(contentX, contentY + rowH, btnW, rowH - 2, "High-Quality Effects", cfg.highQualityEffects, v -> cfg.highQualityEffects = v);
                addToggle(contentX, contentY + rowH * 2, btnW, rowH - 2, "Animation Effects", cfg.enableAbilityAnimations, v -> cfg.enableAbilityAnimations = v);
            }
            case PERFORMANCE -> {
                addToggle(contentX, contentY, btnW, rowH - 2, "Performance Mode", cfg.performanceMode, v -> cfg.performanceMode = v);
                addToggle(contentX, contentY + rowH, btnW, rowH - 2, "Reduced Animation Quality", cfg.reducedAnimationQuality, v -> cfg.reducedAnimationQuality = v);
                this.addRenderableWidget(Button.builder(
                        Component.literal("Effect Distance: " + cfg.effectDistance + "m"),
                        btn -> {
                            cfg.effectDistance = cfg.effectDistance >= 64 ? 16 : cfg.effectDistance + 16;
                            btn.setMessage(Component.literal("Effect Distance: " + cfg.effectDistance + "m"));
                        }).bounds(contentX, contentY + rowH * 2, btnW, rowH - 2).build());
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("Save & Close"), btn -> {
            CustomItemConfig.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(this.panelX + (this.panelW - 96) / 2, this.panelY + this.panelH - 22, 96, 16).build());
    }

    private void addToggle(int x, int y, int w, int h, String label, boolean initial, java.util.function.Consumer<Boolean> setter) {
        final boolean[] state = {initial};
        this.addRenderableWidget(Button.builder(
                Component.literal(label + ": " + (state[0] ? "ON" : "OFF")),
                btn -> {
                    state[0] = !state[0];
                    setter.accept(state[0]);
                    btn.setMessage(Component.literal(label + ": " + (state[0] ? "ON" : "OFF")));
                }).bounds(x, y, w, h).build());
    }

    private void addKeybindButton(int x, int y, int w, int h, String label, String targetId, int currentKey) {
        boolean listening = targetId.equals(this.listeningKeyTarget);
        String keyText = listening ? "> Press Any Key <" : "[" + AbilityKeybindManager.formatKey(currentKey) + "]";
        this.addRenderableWidget(Button.builder(
                Component.literal(label + ": " + keyText),
                btn -> {
                    this.listeningKeyTarget = targetId;
                    this.rebuildWidgets();
                }).bounds(x, y, w, h).build());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.listeningKeyTarget != null) {
            int key = event.key();
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                key = GLFW.GLFW_KEY_R;
            }
            CustomItemConfig cfg = CustomItemConfig.get();
            switch (this.listeningKeyTarget) {
                case "universal" -> cfg.universalAbilityKey = key;
                case "secondary" -> cfg.secondaryAbilityKey = key;
                case "utility" -> cfg.utilityAbilityKey = key;
                case "inventory" -> cfg.openInventoryKey = key;
            }
            this.listeningKeyTarget = null;
            CustomItemConfig.save();
            this.rebuildWidgets();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x90000000);
        graphics.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF202020);
        graphics.fill(panelX + 1, panelY + 1, panelX + panelW - 1, panelY + panelH - 1, 0xFF383838);
        graphics.fill(panelX + 3, panelY + 3, panelX + panelW - 3, panelY + panelH - 3, 0xFF1A1A1A);

        graphics.drawCenteredString(this.font, "Custom Arsenal — " + activeSection.getLabel(),
                this.width / 2, this.panelY + 7, 0xFFFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        CustomItemConfig.save();
        this.minecraft.setScreen(this.parent);
    }
}
