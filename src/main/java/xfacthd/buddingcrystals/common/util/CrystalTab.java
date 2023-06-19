package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CrystalTab extends CreativeModeTab
{
    public static final Component TAB_TITLE = Component.translatable("itemGroup.buddingcrystals");
    private static final int SWITCH_INTERVAL = 1500;
    private static final Supplier<List<ItemStack>> ICON_ITEMS = Suppliers.memoize(() ->
            BCContent.allSets().stream()
                    .filter(CrystalSet::isActive)
                    .map(CrystalSet::getCluster)
                    .map(ItemStack::new)
                    .toList()
    );
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, BuddingCrystals.MOD_ID
    );

    public CrystalTab(CreativeModeTab.Builder builder)
    {
        super(builder);
    }

    @Override
    public ItemStack getIconItem()
    {
        List<ItemStack> list = ICON_ITEMS.get();
        int idx = (int)(System.currentTimeMillis() / SWITCH_INTERVAL) % list.size();
        return list.get(idx);
    }



    public static void registerCreativeTab()
    {
        CREATIVE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());

        CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                .title(TAB_TITLE)
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .withTabFactory(CrystalTab::new)
                .displayItems((params, output) ->
                {
                    for (CrystalSet set : BCContent.allSets())
                    {
                        if (set.isActive())
                        {
                            set.blocks().forEach(output::accept);
                        }
                    }
                    output.accept(BCContent.CRYSTAL_CATALYST.get());
                })
                .build()
        );
    }
}
