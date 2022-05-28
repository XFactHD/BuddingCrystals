package xfacthd.buddingcrystals.common.datagen.providers;

import io.github.lukebemish.dynamic_asset_generator.datagen.ImageSource;
import io.github.lukebemish.dynamic_asset_generator.forge.ForgeTextureConfigProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingTextureConfigProvider extends ForgeTextureConfigProvider
{
    private static final ResourceLocation FALLBACK_TEXTURE = bcRl("block/fallback");

    public BuddingTextureConfigProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper, BuddingCrystals.MOD_ID);
    }

    @Override
    public void addConfigs()
    {
        BCContent.ALL_SETS.forEach(this::defaultConfigs);

        config("crystal_catalyst").output(bcRl("item/crystal_catalyst")).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("item/blaze_powder")))
                        .background(fileSource(mcRl("item/amethyst_shard")))
                        .stretchPaletted(true)
        );
    }

    private void defaultConfigs(CrystalSet set)
    {
        String name = set.getName();

        config("budding/" + name).output(bcRl("block/budding/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/budding_amethyst")))
                        .background(materialSource(set.getBuddingSourceTexture()))
                        .stretchPaletted(true)
        );

        ImageSource materialSource = materialSource(set.getCrystalSourceTexture());

        config("small_bud/" + name).output(bcRl("block/small_bud/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/small_amethyst_bud")))
                        .background(materialSource)
                        .stretchPaletted(true)
        );

        config("medium_bud/" + name).output(bcRl("block/medium_bud/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/medium_amethyst_bud")))
                        .background(materialSource)
                        .stretchPaletted(true)
        );

        config("large_bud/" + name).output(bcRl("block/large_bud/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/large_amethyst_bud")))
                        .background(materialSource)
                        .stretchPaletted(true)
        );

        config("cluster/" + name).output(bcRl("block/cluster/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/amethyst_cluster")))
                        .background(materialSource)
                        .stretchPaletted(true)
        );
    }

    private ImageSource materialSource(ResourceLocation material)
    {
        if (material.getNamespace().equals("minecraft"))
        {
            return fileSource(material);
        }
        else
        {
            return fallbackSource()
                    .original(fileSource(material))
                    .fallback(fileSource(FALLBACK_TEXTURE));
        }
    }

    private static ResourceLocation bcRl(String path) { return rl(BuddingCrystals.MOD_ID, path); }

    private static ResourceLocation mcRl(String path) { return rl("minecraft", path); }

    private static ResourceLocation rl(String namespace, String path) { return new ResourceLocation(namespace, path); }
}
