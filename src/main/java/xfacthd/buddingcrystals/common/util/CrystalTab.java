package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.List;
import java.util.function.Supplier;

public final class CrystalTab extends CreativeModeTab
{
    private static final int SWITCH_INTERVAL = 1500;
    private static final Supplier<List<ItemStack>> ICON_ITEMS = Suppliers.memoize(() ->
            BCContent.allSets().stream()
                    .filter(CrystalSet::isActive)
                    .map(CrystalSet::getCluster)
                    .map(ItemStack::new)
                    .toList()
    );

    public CrystalTab() { super(BuddingCrystals.MOD_ID); }

    @Override
    public ItemStack makeIcon() { return ItemStack.EMPTY; }

    @Override
    public ItemStack getIconItem()
    {
        List<ItemStack> list = ICON_ITEMS.get();
        int idx = (int)(System.currentTimeMillis() / SWITCH_INTERVAL) % list.size();
        return list.get(idx);
    }
}
