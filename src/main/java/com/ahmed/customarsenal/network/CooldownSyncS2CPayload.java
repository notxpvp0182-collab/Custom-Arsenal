package com.ahmed.customarsenal.network;

import com.ahmed.customarsenal.CustomArsenalMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CooldownSyncS2CPayload(String abilityId, int cooldownTicks) implements CustomPacketPayload {
    public static final Type<CooldownSyncS2CPayload> ID = new Type<>(CustomArsenalMod.id("cooldown_sync"));
    public static final StreamCodec<FriendlyByteBuf, CooldownSyncS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CooldownSyncS2CPayload::abilityId,
            ByteBufCodecs.VAR_INT, CooldownSyncS2CPayload::cooldownTicks,
            CooldownSyncS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
