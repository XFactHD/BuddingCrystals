package xfacthd.buddingcrystals.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.List;

public final class SimplePackInfo
{
    public static Pack.Info of(PackResources pack)
    {
        return new Pack.Info(
                Component.literal(pack.packId()),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                List.of(),
                true
        );
    }



    private SimplePackInfo() { }
}
