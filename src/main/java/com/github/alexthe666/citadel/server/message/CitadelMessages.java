package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.CitadelConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class CitadelMessages {
    public static final CustomPacketPayload.Type<AnimationMessage> ANIMATION_TYPE = new CustomPacketPayload.Type<>(CitadelConstants.id("animation"));
    public static final StreamCodec<ByteBuf, AnimationMessage> ANIMATION_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        AnimationMessage::getEntityID,
        ByteBufCodecs.INT,
        AnimationMessage::getIndex,
        AnimationMessage::new
    );

    public static final CustomPacketPayload.Type<DanceJukeboxMessage> DANCE_JUKEBOX_TYPE = new CustomPacketPayload.Type<>(CitadelConstants.id("dance_jukebox"));
    public static final StreamCodec<ByteBuf, DanceJukeboxMessage> DANCE_JUKEBOX_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        DanceJukeboxMessage::getEntityID,
        ByteBufCodecs.BOOL,
        DanceJukeboxMessage::isDance,
        BlockPos.STREAM_CODEC,
        DanceJukeboxMessage::getJukeBox,
        DanceJukeboxMessage::new
    );

    public static final CustomPacketPayload.Type<PropertiesMessage> PROPERTIES_TYPE = new CustomPacketPayload.Type<>(CitadelConstants.id("properties"));
    public static final StreamCodec<ByteBuf, PropertiesMessage> PROPERTIES_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        PropertiesMessage::getPropertyID,
        ByteBufCodecs.COMPOUND_TAG,
        PropertiesMessage::getCompound,
        ByteBufCodecs.INT,
        PropertiesMessage::getEntityID,
        PropertiesMessage::new
    );

    public static final CustomPacketPayload.Type<SyncClientTickRateMessage> SYNC_CLIENT_TICK_RATE_TYPE = new CustomPacketPayload.Type<>(CitadelConstants.id("sync_client_tick_rate"));
    public static final StreamCodec<ByteBuf, SyncClientTickRateMessage> SYNC_CLIENT_TICK_RATE_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG,
        SyncClientTickRateMessage::getCompound,
        SyncClientTickRateMessage::new
    );
}
