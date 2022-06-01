package xfacthd.buddingcrystals.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.util.BuddingPalettePlan;
import xfacthd.buddingcrystals.client.dynpack.BuddingResourcePack;
import xfacthd.buddingcrystals.common.BCContent;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BCClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        BCContent.allClusters().forEach(block ->
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout())
        );
    }

    @SubscribeEvent
    public static void onAddClientPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer, packConstructor) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new BuddingResourcePack();

                packConsumer.accept(Pack.create(
                        BuddingCrystals.MOD_ID + "_json_crystals",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                ));
            });
        }
    }

    static
    {
        BCContent.allSets().forEach(BuddingPalettePlan::plan);
        BuddingPalettePlan.planCatalyst();
    }



    private BCClient() {  }
}
