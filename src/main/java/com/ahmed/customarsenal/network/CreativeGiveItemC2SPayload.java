package com.ahmed.customarsenal.network;

import com.ahmed.customarsenal.CustomArsenalMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CreativeGiveItemC2SPayload(String itemId, int count) implements CustomPacketPayload {
    public static final Type<CreativeGiveItemC2SPayload> ID = new Type<>(CustomArsenalMod.id("creative_give_item"));
    public static final StreamCodec<FriendlyByteBuf, CreativeGiveItemC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CreativeGiveItemC2SPayload::itemId,
            ByteBufCodecs.VAR_INT, CreativeGiveItemC2SPayload::count,
            CreativeGiveItemC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
