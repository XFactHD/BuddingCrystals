package xfacthd.buddingcrystals;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.BuddingServerPack;
import xfacthd.buddingcrystals.common.util.*;

// TODO: implement config phase task for crystal definition parity when the network refactor is done
@Mod(BuddingCrystals.MOD_ID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BuddingCrystals
{
    public static final String MOD_ID = "buddingcrystals";

    public BuddingCrystals(IEventBus modBus)
    {
        BCContent.register(modBus);
        CrystalTab.registerCreativeTab();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        BCContent.loadedSets().forEach(CrystalSet::validate);
    }

    @SubscribeEvent
    public static void onAddServerPack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer) ->
            {
                PackResources pack = new BuddingServerPack();

                packConsumer.accept(Pack.create(
                        MOD_ID + "_json_crystals",
                        Component.literal("BuddingCrystals - JSON Crystals"),
                        true,
                        new SimpleResourcesSupplier(pack),
                        SimplePackInfo.of(pack),
                        Pack.Position.BOTTOM,
                        true,
                        PackSource.DEFAULT
                ));
            });
        }
    }
}
