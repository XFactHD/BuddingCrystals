package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.*;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    private static final ExistingFileHelper.ResourceType TEXTURE_TYPE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> holderProvider = event.getLookupProvider();
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

        gen.addProvider(event.includeClient(), new BuddingStateProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingLanguageProvider(output));
        gen.addProvider(event.includeServer(), new BuddingBlockTagsProvider(output, holderProvider, fileHelper));
        gen.addProvider(event.includeServer(), new BuddingLootTableProvider(output));
        gen.addProvider(event.includeServer(), new BuddingRecipeProvider(output));
    }

    private static ResourceLocation bcRl(String path) { return new ResourceLocation(BuddingCrystals.MOD_ID, path); }



    private GeneratorHandler() { }
}
