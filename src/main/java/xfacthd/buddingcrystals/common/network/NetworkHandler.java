package xfacthd.buddingcrystals.common.network;

import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.network.payload.AckCrystalSetsPayload;
import xfacthd.buddingcrystals.common.network.payload.SyncCrystalSetsPayload;
import xfacthd.buddingcrystals.common.network.task.CrystalSetsConfigTask;

public final class NetworkHandler
{
    public static void onRegisterPayloads(final RegisterPayloadHandlerEvent event)
    {
        event.registrar(BuddingCrystals.MOD_ID)
                .configuration(
                        SyncCrystalSetsPayload.ID,
                        SyncCrystalSetsPayload::read,
                        handler -> handler.client(CrystalSetsConfigTask::handleSync)
                )
                .configuration(
                        AckCrystalSetsPayload.ID,
                        buf -> new AckCrystalSetsPayload(),
                        handler -> handler.server(AckCrystalSetsPayload::handle)
                );
    }

    public static void onCollectConfigTasks(final OnGameConfigurationEvent event)
    {
        if (!event.getListener().getConnection().isMemoryConnection())
        {
            event.register(new CrystalSetsConfigTask());
        }
    }



    private NetworkHandler() { }
}
