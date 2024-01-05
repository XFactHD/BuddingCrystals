package xfacthd.buddingcrystals.common.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xfacthd.buddingcrystals.BuddingCrystals;

import java.util.HashSet;
import java.util.Set;

public record SyncCrystalSetsPayload(Set<Entry> crystalEntries) implements CustomPacketPayload
{
    public static final ResourceLocation ID = BuddingCrystals.rl("sync_crystal_sets");

    public static SyncCrystalSetsPayload read(FriendlyByteBuf buf)
    {
        return new SyncCrystalSetsPayload(buf.readCollection(HashSet::new, Entry::read));
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeCollection(crystalEntries, Entry::write);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }



    public record Entry(String name, String mod, boolean active)
    {
        static Entry read(FriendlyByteBuf buf)
        {
            return new Entry(buf.readUtf(), buf.readUtf(), buf.readBoolean());
        }

        static void write(FriendlyByteBuf buf, Entry entry)
        {
            buf.writeUtf(entry.name);
            buf.writeUtf(entry.mod);
            buf.writeBoolean(entry.active);
        }
    }
}
