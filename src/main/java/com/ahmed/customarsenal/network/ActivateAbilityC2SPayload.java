package com.ahmed.customarsenal.network;

import com.ahmed.customarsenal.CustomArsenalMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ActivateAbilityC2SPayload(String itemId, String abilityId, boolean mainHand) implements CustomPacketPayload {
    public static final Type<ActivateAbilityC2SPayload> ID = new Type<>(CustomArsenalMod.id("activate_ability"));
    public static final StreamCodec<FriendlyByteBuf, ActivateAbilityC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ActivateAbilityC2SPayload::itemId,
            ByteBufCodecs.STRING_UTF8, ActivateAbilityC2SPayload::abilityId,
            ByteBufCodecs.BOOL, ActivateAbilityC2SPayload::mainHand,
            ActivateAbilityC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
