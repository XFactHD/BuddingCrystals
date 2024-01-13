package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.datagen.providers.*;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> holderProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        // Trick the generators into thinking the texture exists
        fileHelper.trackGenerated(new ResourceLocation(BuddingCrystals.MOD_ID, "item/crystal_catalyst"), ModelProvider.TEXTURE);

        gen.addProvider(event.includeClient(), new BuddingItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingLanguageProvider(output));
        gen.addProvider(event.includeServer(), new BuddingRecipeProvider(output, holderProvider));
    }



    private GeneratorHandler() { }
}
