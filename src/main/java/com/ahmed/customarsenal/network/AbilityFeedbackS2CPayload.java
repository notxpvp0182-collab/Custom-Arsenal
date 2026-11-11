package com.ahmed.customarsenal.network;

import com.ahmed.customarsenal.CustomArsenalMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AbilityFeedbackS2CPayload(String abilityId, boolean mainHand) implements CustomPacketPayload {
    public static final Type<AbilityFeedbackS2CPayload> ID = new Type<>(CustomArsenalMod.id("ability_feedback"));
    public static final StreamCodec<FriendlyByteBuf, AbilityFeedbackS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AbilityFeedbackS2CPayload::abilityId,
            ByteBufCodecs.BOOL, AbilityFeedbackS2CPayload::mainHand,
            AbilityFeedbackS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
