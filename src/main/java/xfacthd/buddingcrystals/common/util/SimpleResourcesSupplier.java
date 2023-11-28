package xfacthd.buddingcrystals.common.util;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;

public record SimpleResourcesSupplier(PackResources pack) implements Pack.ResourcesSupplier
{
    @Override
    public PackResources openPrimary(String id)
    {
        return pack;
    }

    @Override
    public PackResources openFull(String id, Pack.Info info)
    {
        return pack;
    }
}
