package xfacthd.buddingcrystals.common.util;

import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.List;

public final class SimplePackInfo
{
    public static Pack.Info of(PackResources pack)
    {
        WorldVersion version = SharedConstants.getCurrentVersion();
        return new Pack.Info(
                Component.literal(pack.packId()),
                version.getPackVersion(PackType.SERVER_DATA),
                version.getPackVersion(PackType.CLIENT_RESOURCES),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                List.of(),
                true
        );
    }



    private SimplePackInfo() { }
}
