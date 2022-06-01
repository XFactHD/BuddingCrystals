package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.*;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    private static final ExistingFileHelper.ResourceType TEXTURE_TYPE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        // Trick the generators into thinking the textures exist
        fileHelper.trackGenerated(bcRl("item/crystal_catalyst"), TEXTURE_TYPE);
        BCContent.builtinSets().forEach(set ->
        {
            String name = set.getName();
            fileHelper.trackGenerated(bcRl("block/budding/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/small_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/medium_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/large_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/cluster/" + name), TEXTURE_TYPE);
        });

        gen.addProvider(new BuddingStateProvider(gen, fileHelper));
        gen.addProvider(new BuddingItemModelProvider(gen, fileHelper));
        gen.addProvider(new BuddingLanguageProvider(gen));
        gen.addProvider(new BuddingBlockTagsProvider(gen, fileHelper));
        gen.addProvider(new BuddingLootTableProvider(gen));
        gen.addProvider(new BuddingRecipeProvider(gen));
    }

    private static ResourceLocation bcRl(String path) { return new ResourceLocation(BuddingCrystals.MOD_ID, path); }



    private GeneratorHandler() { }
}
