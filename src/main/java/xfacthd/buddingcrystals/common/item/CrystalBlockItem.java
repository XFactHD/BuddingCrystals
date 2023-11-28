package xfacthd.buddingcrystals.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public final class CrystalBlockItem extends BlockItem
{
    private final String compatMod;

    public CrystalBlockItem(Block block, String compatMod)
    {
        super(block, new Properties());
        this.compatMod = compatMod;
    }

    public String getCompatMod()
    {
        return compatMod;
    }
}
