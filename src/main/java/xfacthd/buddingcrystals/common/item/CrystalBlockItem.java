package xfacthd.buddingcrystals.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import xfacthd.buddingcrystals.BuddingCrystals;

public final class CrystalBlockItem extends BlockItem
{
    private CrystalBlockItem(Block block, Properties properties) { super(block, properties); }

    public static CrystalBlockItem make(Block block, String compatMod)
    {
        Properties props = new Properties();
        if (ModList.get().isLoaded(compatMod))
        {
            props.tab(BuddingCrystals.CREATIVE_TAB);
        }
        return new CrystalBlockItem(block, props);
    }
}
