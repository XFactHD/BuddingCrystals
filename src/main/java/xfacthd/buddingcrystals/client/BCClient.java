package xfacthd.buddingcrystals.client;

import io.github.lukebemish.dynamic_asset_generator.client.api.JsonReaderAPI;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.util.FallbackTextureReader;
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

    static
    {
        // This must happen very early
        JsonReaderAPI.registerTexSourceReadingType(
                new ResourceLocation(BuddingCrystals.MOD_ID, "fallback_texture"),
                new FallbackTextureReader()
        );
    }



    private BCClient() {  }
}
