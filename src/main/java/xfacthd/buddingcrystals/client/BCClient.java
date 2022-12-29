package xfacthd.buddingcrystals.client;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.util.BuddingPalettePlanner;
import xfacthd.buddingcrystals.client.dynpack.BuddingResourcePack;
import xfacthd.buddingcrystals.client.util.ExportCommand;
import xfacthd.buddingcrystals.common.BCContent;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BCClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener(BCClient::onRegisterClientCommands);
    }

    @SubscribeEvent
    public static void onAddClientPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new BuddingResourcePack();

                packConsumer.accept(Pack.create(
                        BuddingCrystals.MOD_ID + "_json_crystals",
                        Component.literal("BuddingCrystals - JSON Crystals"),
                        true,
                        s -> pack,
                        new Pack.Info(
                                Component.literal(pack.packId()),
                                BuddingCrystals.SERVER_PACK_FORMAT,
                                BuddingCrystals.RESOURCE_PACK_FORMAT,
                                FeatureFlagSet.of(),
                                true
                        ),
                        PackType.CLIENT_RESOURCES,
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
