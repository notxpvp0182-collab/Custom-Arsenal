package com.ahmed.customarsenal.ability;

import com.ahmed.customarsenal.config.CustomItemConfig;
import com.ahmed.customarsenal.item.CustomItem;
import com.ahmed.customarsenal.network.AbilityFeedbackS2CPayload;
import com.ahmed.customarsenal.network.CooldownSyncS2CPayload;
import com.ahmed.customarsenal.util.CustomSoundManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class AbilityManager {

    public record ResolvedAbility(CustomAbility ability, InteractionHand hand, ItemStack stack, CustomItem item) {}

    /**
     * Contextually resolves which ability should activate for the given player and keySlot ("slot_1", "slot_2", "slot_3", "shield").
     * Strictly checks MAIN_HAND first, then OFF_HAND, and verifies that the item's unique internal ID owns the ability
     * and that the ability's ActivationHand setting allows that hand.
     */
    public static Optional<ResolvedAbility> resolveContextualAbility(Player player, String keySlot) {
        // 1. Check Main Hand
        Optional<ResolvedAbility> mainMatch = checkHand(player, InteractionHand.MAIN_HAND, keySlot);
        if (mainMatch.isPresent()) {
            return mainMatch;
        }
        // 2. Check Offhand
        return checkHand(player, InteractionHand.OFF_HAND, keySlot);
    }

    private static Optional<ResolvedAbility> checkHand(Player player, InteractionHand hand, String keySlot) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof CustomItem customItem)) {
            return Optional.empty();
        }
        for (CustomAbility ability : customItem.getCustomData().abilities()) {
            boolean slotMatches = ability.keySlot().equals(keySlot)
                    || ("shield".equals(keySlot) && "guardian_barrier".equals(ability.id()));
            if (slotMatches && CustomItemConfig.get().isAbilityEnabled(ability.id())) {
                if (ability.getEffectiveHand().matches(hand)) {
                    return Optional.of(new ResolvedAbility(ability, hand, stack, customItem));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Authoritative server-side execution of a contextual ability.
     */
    public static void tryExecuteServer(ServerPlayer player, String itemId, String abilityId, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof CustomItem customItem)) {
            return;
        }
        // Verify unique internal item ID matches
        if (!customItem.getCustomData().id().equals(itemId)) {
            return;
        }
        CustomAbility ability = customItem.getCustomData().getAbilityById(abilityId);
        if (ability == null || !CustomItemConfig.get().isAbilityEnabled(ability.id())) {
            return;
        }
        // Verify ActivationHand rule
        if (!ability.getEffectiveHand().matches(hand)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        long now = level.getGameTime();
        if (AbilityCooldownManager.isOnCooldown(player.getUUID(), ability.id(), now)) {
            long remTicks = AbilityCooldownManager.getRemainingTicks(player.getUUID(), ability.id(), now);
            float seconds = remTicks / 20.0f;
            player.displayClientMessage(
                    Component.literal(ability.displayName() + " is on cooldown (" + String.format("%.1fs", seconds) + ")")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        AbilityContext context = new AbilityContext(player, level, hand, stack, customItem);
        if (ability.condition() != null && !ability.condition().test(context)) {
            return;
        }

        boolean executed = ability.executor().execute(context);
        if (executed) {
            int effectiveCooldown = ability.getEffectiveCooldownTicks();
            AbilityCooldownManager.setCooldown(player.getUUID(), ability.id(), now, effectiveCooldown);

            CustomSoundManager.playSound(level, player.getX(), player.getY(), player.getZ(),
                    ability.sound(), 1.0f, 1.0f);

            player.swing(hand, true);

            ServerPlayNetworking.send(player, new CooldownSyncS2CPayload(ability.id(), effectiveCooldown));
            ServerPlayNetworking.send(player, new AbilityFeedbackS2CPayload(ability.id(), hand == InteractionHand.MAIN_HAND));
        }
    }
}
