package xfacthd.buddingcrystals.common.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.network.task.CrystalSetsConfigTask;

public record AckCrystalSetsPayload() implements CustomPacketPayload
{
    public static final ResourceLocation ID = BuddingCrystals.rl("ack_crystal_sets");

    @Override
    public void write(FriendlyByteBuf buffer) { }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void handle(ConfigurationPayloadContext ctx)
    {
        ctx.taskCompletedHandler().onTaskCompleted(CrystalSetsConfigTask.TYPE);
    }
}
