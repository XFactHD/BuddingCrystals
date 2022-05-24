package xfacthd.buddingcrystals;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalTab;

@Mod(BuddingCrystals.MOD_ID)
public final class BuddingCrystals
{
    public static final String MOD_ID = "buddingcrystals";

    public static final CreativeModeTab CREATIVE_TAB = new CrystalTab();

    public BuddingCrystals()
    {
        BCContent.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
