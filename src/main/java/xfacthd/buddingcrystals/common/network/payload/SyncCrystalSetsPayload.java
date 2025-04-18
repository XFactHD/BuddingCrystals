package xfacthd.buddingcrystals.common.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xfacthd.buddingcrystals.BuddingCrystals;

import java.util.HashSet;
import java.util.Set;

public record SyncCrystalSetsPayload(Set<Entry> crystalEntries) implements CustomPacketPayload
{
    public static final Type<SyncCrystalSetsPayload> TYPE = new Type<>(BuddingCrystals.rl("sync_crystal_sets"));
    public static final StreamCodec<ByteBuf, SyncCrystalSetsPayload> STREAM_CODEC = ByteBufCodecs.collection(
            size -> (Set<Entry>) new HashSet<Entry>(size), Entry.STREAM_CODEC
    ).map(SyncCrystalSetsPayload::new, SyncCrystalSetsPayload::crystalEntries);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }



    public record Entry(String name, String mod, boolean active)
    {
        public static StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                Entry::name,
                ByteBufCodecs.STRING_UTF8,
                Entry::mod,
                ByteBufCodecs.BOOL,
                Entry::active,
                Entry::new
        );
    }
}
