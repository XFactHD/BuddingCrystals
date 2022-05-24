package xfacthd.buddingcrystals.common.util;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public final class BudSet
{
    final RegistryObject<Block> smallBud;
    final RegistryObject<Block> mediumBud;
    final RegistryObject<Block> largeBud;
    final RegistryObject<Block> cluster;

    BudSet(RegistryObject<Block> smallBud, RegistryObject<Block> mediumBud, RegistryObject<Block> largeBud, RegistryObject<Block> cluster)
    {
        this.smallBud = smallBud;
        this.mediumBud = mediumBud;
        this.largeBud = largeBud;
        this.cluster = cluster;
    }

    public Block getSmallBud() { return smallBud.get(); }

    public Block getMediumBud() { return mediumBud.get(); }

    public Block getLargeBud() { return largeBud.get(); }

    public Block getCluster() { return cluster.get(); }

    public List<Block> blocks()
    {
        return List.of(
                smallBud.get(),
                mediumBud.get(),
                largeBud.get(),
                cluster.get()
        );
    }
}
