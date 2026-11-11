package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.ability.AbilityCooldownManager;
import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.client.keybind.AbilityKeybindManager;
import com.ahmed.customarsenal.config.CustomItemConfig;
import com.ahmed.customarsenal.item.CustomItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CustomHudOverlay {

    private record ActiveDisplay(CustomAbility ability, String keyName, AbilityCooldownManager.CooldownEntry cd) {}

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!CustomItemConfig.get().showCooldownIndicators) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.options.hideGui) return;

        long now = client.level.getGameTime();
        List<ActiveDisplay> displays = new ArrayList<>(4);

        collectHandAbilities(client.player.getMainHandItem(), InteractionHand.MAIN_HAND, now, displays);
        collectHandAbilities(client.player.getOffhandItem(), InteractionHand.OFF_HAND, now, displays);

        if (displays.isEmpty()) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        int startX = screenWidth / 2 + 100;
        int startY = screenHeight - 22 - (displays.size() - 1) * 16;

        for (int i = 0; i < displays.size(); i++) {
            ActiveDisplay d = displays.get(i);
            int y = startY + (i * 16);

            // Vanilla-styled clean dark HUD pill
            graphics.fill(startX, y, startX + 108, y + 14, 0xAA101010);
            graphics.renderOutline(startX, y, 108, 14, 0xFF3A3A3A);

            if (d.cd() != null) {
                float progress = d.cd().getProgress(now);
                int barWidth = Math.round(106 * progress);
                graphics.fill(startX + 1, y + 1, startX + 1 + barWidth, y + 13, 0x88AA2222);
                String text = "[" + d.keyName() + "] " + String.format("%.1fs", d.cd().getRemainingSeconds(now));
                graphics.drawString(client.font, text, startX + 4, y + 3, 0xFFFF7777, true);
            } else {
                String text = "[" + d.keyName() + "] " + d.ability().displayName();
                if (text.length() > 18) text = text.substring(0, 17) + "…";
                graphics.drawString(client.font, text, startX + 4, y + 3, 0xFF55FF55, true);
            }
        }
    }

    private static void collectHandAbilities(ItemStack stack, InteractionHand hand, long now, List<ActiveDisplay> out) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CustomItem customItem)) return;
        for (CustomAbility ability : customItem.getCustomData().abilities()) {
            if (!CustomItemConfig.get().isAbilityEnabled(ability.id())) continue;
            if (!ability.getEffectiveHand().matches(hand)) continue;
            String keyName = AbilityKeybindManager.getBoundKeyName(ability.keySlot());
            AbilityCooldownManager.CooldownEntry cd = AbilityCooldownManager.getClientEntry(ability.id(), now);
            out.add(new ActiveDisplay(ability, keyName, cd));
        }
    }
}
