package com.ahmed.customarsenal.ability;

import com.ahmed.customarsenal.item.CustomItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record AbilityContext(
        ServerPlayer player,
        ServerLevel level,
        InteractionHand hand,
        ItemStack stack,
        CustomItem customItem
) {}
