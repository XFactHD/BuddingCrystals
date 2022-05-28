package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.datagen.providers.*;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        gen.addProvider(new BuddingTextureConfigProvider(gen, fileHelper));
        gen.addProvider(new BuddingStateProvider(gen, fileHelper));
        gen.addProvider(new BuddingItemModelProvider(gen, fileHelper));
        gen.addProvider(new BuddingLanguageProvider(gen));
        gen.addProvider(new BuddingBlockTagsProvider(gen, fileHelper));
        gen.addProvider(new BuddingLootTableProvider(gen));
        gen.addProvider(new BuddingRecipeProvider(gen));
    }



    private GeneratorHandler() { }
}
