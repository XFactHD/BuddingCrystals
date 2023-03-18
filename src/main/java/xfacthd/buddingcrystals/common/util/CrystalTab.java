package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

    public CrystalTab(CreativeModeTab.Builder builder) { super(builder); }

    @Override
    public ItemStack getIconItem()
    {
        List<ItemStack> list = ICON_ITEMS.get();
        int idx = (int)(System.currentTimeMillis() / SWITCH_INTERVAL) % list.size();
        return list.get(idx);
    }



    @SubscribeEvent
    public static void onRegisterTab(final CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(new ResourceLocation(BuddingCrystals.MOD_ID, "main"), builder ->
            builder.title(TAB_TITLE)
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
        );
    }
}
