package com.sitael.networking;

import com.sitael.ExampleMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SendProtobuffDataC2SPayload(byte[] data) implements CustomPayload {
    public static final Identifier GIVE_GLOWING_EFFECT_PAYLOAD_ID = Identifier.of(ExampleMod.MOD_ID, "send_data");
    public static final CustomPayload.Id<SendProtobuffDataC2SPayload> ID = new CustomPayload.Id<>(GIVE_GLOWING_EFFECT_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SendProtobuffDataC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.BYTE_ARRAY, SendProtobuffDataC2SPayload::data, SendProtobuffDataC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
