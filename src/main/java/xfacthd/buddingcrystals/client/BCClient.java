package xfacthd.buddingcrystals.client;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.util.BuddingPalettePlanner;
import xfacthd.buddingcrystals.client.dynpack.BuddingResourcePack;
import xfacthd.buddingcrystals.client.util.ExportCommand;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.SimplePackInfo;
import xfacthd.buddingcrystals.common.util.SimpleResourcesSupplier;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BCClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.addListener(BCClient::onRegisterClientCommands);
    }

    @SubscribeEvent
    public static void onAddClientPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer) ->
            {
                PackResources pack = new BuddingResourcePack();

                packConsumer.accept(Pack.create(
                        BuddingCrystals.MOD_ID + "_json_crystals",
                        Component.literal("BuddingCrystals - JSON Crystals"),
                        true,
                        new SimpleResourcesSupplier(pack),
                        SimplePackInfo.of(pack),
                        Pack.Position.BOTTOM,
                        true,
                        PackSource.DEFAULT
                ));
            });
        }
    }

    public static void onRegisterClientCommands(final RegisterClientCommandsEvent event)
    {
        ExportCommand.register(event.getDispatcher());
    }

    static
    {
        BCContent.allSets().forEach(BuddingPalettePlanner::plan);
        BuddingPalettePlanner.planCatalyst();
    }



    private BCClient() {  }
}
