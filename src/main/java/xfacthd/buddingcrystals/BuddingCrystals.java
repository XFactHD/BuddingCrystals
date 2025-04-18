package xfacthd.buddingcrystals;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.DynDataPlanner;
import xfacthd.buddingcrystals.common.network.NetworkHandler;
import xfacthd.buddingcrystals.common.util.CommonConfig;
import xfacthd.buddingcrystals.common.util.CrystalSet;
import xfacthd.buddingcrystals.common.util.CrystalTab;

@Mod(BuddingCrystals.MOD_ID)
public final class BuddingCrystals
{
    public static final String MOD_ID = "buddingcrystals";

    public BuddingCrystals(IEventBus modBus, ModContainer container)
    {
        BCContent.register(modBus);
        CrystalTab.registerCreativeTab(modBus);

        DynDataPlanner.init();
        BCContent.allActiveSets().forEach(DynDataPlanner::plan);

        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        modBus.register(CommonConfig.INSTANCE);
        modBus.addListener(NetworkHandler::onRegisterPayloads);
        modBus.addListener(BuddingCrystals::onCommonSetup);
        modBus.addListener(DynDataPlanner::onAddDataPackFinders);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        BCContent.loadedSets().forEach(CrystalSet::validate);
    }

    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
