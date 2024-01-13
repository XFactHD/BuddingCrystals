package xfacthd.buddingcrystals.common.util;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public final class BudSet
{
    final Holder<Block> smallBud;
    final Holder<Block> mediumBud;
    final Holder<Block> largeBud;
    final Holder<Block> cluster;

    BudSet(Holder<Block> smallBud, Holder<Block> mediumBud, Holder<Block> largeBud, Holder<Block> cluster)
    {
        this.smallBud = smallBud;
        this.mediumBud = mediumBud;
        this.largeBud = largeBud;
        this.cluster = cluster;
    }

    public Block getSmallBud()
    {
        return smallBud.value();
    }

    public Block getMediumBud()
    {
        return mediumBud.value();
    }

    public Block getLargeBud()
    {
        return largeBud.value();
    }

    public Block getCluster()
    {
        return cluster.value();
    }
}
