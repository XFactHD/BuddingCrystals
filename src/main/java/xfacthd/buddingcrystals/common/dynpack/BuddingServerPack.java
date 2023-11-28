package xfacthd.buddingcrystals.common.dynpack;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class BuddingServerPack extends BuddingPackResources
{
    @SuppressWarnings("deprecation")
    public BuddingServerPack()
    {
        super(PackType.SERVER_DATA, SharedConstants.DATA_PACK_FORMAT);
    }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        CompletableFuture<HolderLookup.Provider> holderProvider = CompletableFuture.supplyAsync(
                VanillaRegistries::createLookup,
                Util.backgroundExecutor()
        );

        //noinspection ConstantConditions
        new DynamicTagGenerator(cache, holderProvider).run(null);
        //noinspection ConstantConditions
        new DynamicRecipeGenerator(cache, holderProvider).run(null);
        new DynamicBlockLoot().run(cache);
    }

    @Override
    public String packId()
    {
        return "BuddingCrystals JSON Crystal Data";
    }
}
