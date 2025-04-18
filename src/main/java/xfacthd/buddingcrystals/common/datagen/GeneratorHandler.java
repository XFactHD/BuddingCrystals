package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingItemModelProvider;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingLanguageProvider;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingRecipeProvider;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Trick the generators into thinking the texture exists
        fileHelper.trackGenerated(BuddingCrystals.rl("item/crystal_catalyst"), ModelProvider.TEXTURE);

        gen.addProvider(event.includeClient(), new BuddingItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingLanguageProvider(output));
        gen.addProvider(event.includeServer(), new BuddingRecipeProvider(output, lookupProvider));
    }



    private GeneratorHandler() { }
}
