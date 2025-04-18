package xfacthd.buddingcrystals.common.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.network.task.CrystalSetsConfigTask;

public final class AckCrystalSetsPayload implements CustomPacketPayload
{
    public static final Type<AckCrystalSetsPayload> TYPE = new Type<>(BuddingCrystals.rl("ack_crystal_sets"));
    public static final AckCrystalSetsPayload INSTANCE = new AckCrystalSetsPayload();
    public static final StreamCodec<ByteBuf, AckCrystalSetsPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AckCrystalSetsPayload() { }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void handle(IPayloadContext ctx)
    {
        ctx.finishCurrentTask(CrystalSetsConfigTask.TYPE);
    }
}
