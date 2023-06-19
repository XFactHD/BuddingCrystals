package xfacthd.buddingcrystals.client.util;

import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TextureGenerator;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.PaletteCombinedSource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReaderSource;
import net.minecraft.resources.ResourceLocation;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingPalettePlanner
{
    private static final AssetResourceCache ASSET_CACHE = ResourceCache.register(new AssetResourceCache(new ResourceLocation(BuddingCrystals.MOD_ID, "assets")));
    private static final ResourceLocation FALLBACK_TEXTURE = rl("forge", "white");
    private static final ResourceLocation EMPTY_TEXTURE = rl("dynamic_asset_generator", "empty");

    public static void plan(CrystalSet set)
    {
        String name = set.getName();

        plan("block/budding/" + name,
                set.isActive() ? set.getBuddingSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/budding_amethyst")
        );

        plan("block/small_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/small_amethyst_bud")
        );
        plan("block/medium_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/medium_amethyst_bud")
        );
        plan("block/large_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/large_amethyst_bud")
        );
        plan("block/cluster/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/amethyst_cluster")
        );
    }

    public static void planCatalyst()
    {
        plan("item/crystal_catalyst", mcRl("item/amethyst_shard"), mcRl("item/blaze_powder"));
    }

    private static void plan(String path, ResourceLocation background, ResourceLocation paletted)
    {
        ASSET_CACHE.planSource(
                new TextureGenerator(
                        bcRl(path),
                        new PaletteCombinedSource.Builder()
                                .setOverlay(new TextureReaderSource.Builder().setPath(EMPTY_TEXTURE).build())
                                .setBackground(new TextureReaderSource.Builder().setPath(background).build())
                                .setPaletted(new TextureReaderSource.Builder().setPath(paletted).build())
                                .setIncludeBackground(false)
                                .setStretchPaletted(true)
                                .setExtendPaletteSize(0)
                                .build()
                )
        );
    }

    private static ResourceLocation bcRl(String path)
    {
        return rl(BuddingCrystals.MOD_ID, path);
    }

    private static ResourceLocation mcRl(String path)
    {
        return rl("minecraft", path);
    }

    private static ResourceLocation rl(String namespace, String path)
    {
        return new ResourceLocation(namespace, path);
    }



    private BuddingPalettePlanner() { }
}
