package xfacthd.buddingcrystals;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.BuddingServerPack;
import xfacthd.buddingcrystals.common.util.*;

@Mod(BuddingCrystals.MOD_ID)
public final class BuddingCrystals
{
    public static final String MOD_ID = "buddingcrystals";

    public static final CreativeModeTab CREATIVE_TAB = new CrystalTab();

    public BuddingCrystals()
    {
        BCContent.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    public void registerRecipeConditions(final RegistryEvent.Register<RecipeSerializer<?>> event)
    {
        CraftingHelper.register(new ConfigCondition.Serializer());
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent event) { BCContent.loadedSets().forEach(CrystalSet::validate); }

    @SubscribeEvent
    public void onAddServerPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer, packConstructor) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new BuddingServerPack();

                packConsumer.accept(Pack.create(
                        MOD_ID + "_json_crystals",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                ));
            });
        }
    }
}
