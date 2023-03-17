package xfacthd.buddingcrystals.common.dynpack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import xfacthd.buddingcrystals.BuddingCrystals;

import java.util.Map;
import java.util.Set;

public final class BuddingServerPack extends BuddingPackResources
{
    public BuddingServerPack() { super(PackType.SERVER_DATA, 9); }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        //noinspection ConstantConditions
        new DynamicTagGenerator(cache).run(null);
        //noinspection ConstantConditions
        new DynamicRecipeGenerator(cache).run(null);
        new DynamicBlockLoot().run(cache);
    }

    @Override
    public String getName() { return "BuddingCrystals JSON Crystal Data"; }
}
