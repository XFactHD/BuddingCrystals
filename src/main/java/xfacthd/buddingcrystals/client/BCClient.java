package xfacthd.buddingcrystals.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.dynpack.DynAssetPlanner;
import xfacthd.buddingcrystals.client.util.ExportCommand;
import xfacthd.buddingcrystals.common.BCContent;

@Mod(value = BuddingCrystals.MOD_ID, dist = Dist.CLIENT)
public final class BCClient
{
    public BCClient(IEventBus modBus)
    {
        DynAssetPlanner.init();
        DynAssetPlanner.plan();
        BCContent.allActiveSets().forEach(DynAssetPlanner::plan);
        DynAssetPlanner.planCatalyst();

        modBus.addListener(DynAssetPlanner::onRegisterClientReloadListeners);

        NeoForge.EVENT_BUS.addListener(BCClient::onRegisterClientCommands);
    }

    private static void onRegisterClientCommands(final RegisterClientCommandsEvent event)
    {
        ExportCommand.register(event.getDispatcher());
    }
}
