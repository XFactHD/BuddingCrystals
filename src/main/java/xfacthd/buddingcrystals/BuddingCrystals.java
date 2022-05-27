package xfacthd.buddingcrystals;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xfacthd.buddingcrystals.common.BCContent;
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
}
