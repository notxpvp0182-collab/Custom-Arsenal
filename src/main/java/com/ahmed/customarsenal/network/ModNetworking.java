package com.ahmed.customarsenal.network;

import com.ahmed.customarsenal.ability.AbilityManager;
import com.ahmed.customarsenal.item.CustomItem;
import com.ahmed.customarsenal.item.CustomItemRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public final class ModNetworking {

    public static void initCommon() {
        PayloadTypeRegistry.playC2S().register(ActivateAbilityC2SPayload.ID, ActivateAbilityC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CreativeGiveItemC2SPayload.ID, CreativeGiveItemC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CooldownSyncS2CPayload.ID, CooldownSyncS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AbilityFeedbackS2CPayload.ID, AbilityFeedbackS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ActivateAbilityC2SPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                InteractionHand hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                AbilityManager.tryExecuteServer(player, payload.itemId(), payload.abilityId(), hand);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CreativeGiveItemC2SPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                if (!player.isCreative()) return;
                CustomItem item = CustomItemRegistry.get(payload.itemId());
                if (item != null) {
                    int amount = Mth.clamp(payload.count(), 1, item.getCustomData().maxStackSize());
                    ItemStack stack = new ItemStack(item, amount);
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                    player.inventoryMenu.broadcastChanges();
                }
            });
        });
    }
}
