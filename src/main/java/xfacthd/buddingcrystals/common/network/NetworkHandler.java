package xfacthd.buddingcrystals.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.network.payload.AckCrystalSetsPayload;
import xfacthd.buddingcrystals.common.network.payload.SyncCrystalSetsPayload;
import xfacthd.buddingcrystals.common.network.task.CrystalSetsConfigTask;

public final class NetworkHandler
{
    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(BuddingCrystals.MOD_ID)
                .configurationToClient(
                        SyncCrystalSetsPayload.TYPE,
                        SyncCrystalSetsPayload.STREAM_CODEC,
                        CrystalSetsConfigTask::handleSync
                )
                .configurationToServer(
                        AckCrystalSetsPayload.TYPE,
                        AckCrystalSetsPayload.STREAM_CODEC,
                        AckCrystalSetsPayload::handle
                );
    }



    private NetworkHandler() { }
}
