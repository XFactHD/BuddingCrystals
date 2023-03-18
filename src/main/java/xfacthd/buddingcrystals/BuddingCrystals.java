package xfacthd.buddingcrystals;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.BuddingServerPack;
import xfacthd.buddingcrystals.common.util.*;

@Mod(BuddingCrystals.MOD_ID)
@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BuddingCrystals
{
    public static final String MOD_ID = "buddingcrystals";
    public static final int SERVER_PACK_FORMAT = 12;
    public static final int RESOURCE_PACK_FORMAT = 13;

    public BuddingCrystals()
    {
        BCContent.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    public static void registerRecipeConditions(final RegisterEvent event)
    {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
        {
            CraftingHelper.register(new ConfigCondition.Serializer());
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) { BCContent.loadedSets().forEach(CrystalSet::validate); }

    @SubscribeEvent
    public static void onAddServerPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new BuddingServerPack();

                packConsumer.accept(Pack.create(
                        MOD_ID + "_json_crystals",
                        Component.literal("BuddingCrystals - JSON Crystals"),
                        true,
                        s -> pack,
                        new Pack.Info(
                                Component.literal(pack.packId()),
                                SERVER_PACK_FORMAT,
                                RESOURCE_PACK_FORMAT,
                                FeatureFlagSet.of(),
                                true
                        ),
                        PackType.SERVER_DATA,
                        Pack.Position.BOTTOM,
                        true,
                        PackSource.DEFAULT
                ));
            });
        }
    }
}
