package xfacthd.buddingcrystals.client.util;

import io.github.lukebemish.dynamic_asset_generator.api.client.AssetResourceCache;
import io.github.lukebemish.dynamic_asset_generator.api.client.generators.DynamicTextureSource;
import io.github.lukebemish.dynamic_asset_generator.api.client.generators.texsources.CombinedPaletteImage;
import io.github.lukebemish.dynamic_asset_generator.api.client.generators.texsources.TextureReader;
import net.minecraft.resources.ResourceLocation;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingPalettePlanner
{
    private static final ResourceLocation FALLBACK_TEXTURE = bcRl("block/fallback");
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
        AssetResourceCache.INSTANCE.planSource(
                new DynamicTextureSource(
                        bcRl(path),
                        new CombinedPaletteImage(
                                new TextureReader(EMPTY_TEXTURE),
                                new TextureReader(background),
                                new TextureReader(paletted),
                                false,
                                true,
                                0
                        )
                )
        );
    }

    private static ResourceLocation bcRl(String path) { return rl(BuddingCrystals.MOD_ID, path); }

    private static ResourceLocation mcRl(String path) { return rl("minecraft", path); }

    private static ResourceLocation rl(String namespace, String path) { return new ResourceLocation(namespace, path); }
}
