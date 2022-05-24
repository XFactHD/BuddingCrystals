package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.builders.ImageSource;
import xfacthd.buddingcrystals.common.datagen.builders.TextureConfigProvider;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingTextureConfigProvider extends TextureConfigProvider
{
    private static final ResourceLocation FALLBACK_TEXTURE = bcRl("block/fallback");

    public BuddingTextureConfigProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper, BuddingCrystals.MOD_ID);
    }

    @Override
    public void addConfigs()
    {
        defaultConfigs(BCContent.REDSTONE, mcRl("item/redstone"));
        defaultConfigs(BCContent.DIAMOND, mcRl("item/diamond"));
        defaultConfigs(BCContent.EMERALD, mcRl("item/emerald"));
        defaultConfigs(BCContent.LAPIS_LAZULI, mcRl("item/lapis_lazuli"));
        defaultConfigs(BCContent.GLOWSTONE, mcRl("item/glowstone_dust"));
        defaultConfigs(BCContent.NETHER_QUARTZ, mcRl("item/quartz"));
        defaultConfigs(BCContent.CERTUS_QUARTZ, rl("ae2", "item/certus_quartz_crystal"));
        defaultConfigs(BCContent.FLUIX, rl("ae2", "item/fluix_crystal"));
        defaultConfigs(BCContent.SALT, rl("mekanism", "item/salt"));
        defaultConfigs(BCContent.FLUORITE, rl("mekanism", "item/fluorite_gem"));

        config("crystal_catalyst").output(bcRl("item/crystal_catalyst")).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("item/blaze_powder")))
                        .background(fileSource(mcRl("item/amethyst_shard")))
                        .stretchPaletted(true)
        );
    }

    private void defaultConfigs(CrystalSet set, ResourceLocation material)
    {
        String name = set.getName();

        ImageSource materialSource;
        if (material.getNamespace().equals("minecraft"))
        {
            materialSource = fileSource(material);
        }
        else
        {
            materialSource = fallbackFileSource().texture(material).fallback(FALLBACK_TEXTURE);
        }

        config("budding/" + name).output(bcRl("block/budding/" + name)).input(
                combinedPalettedImageSource()
                        .overlay(colorSource())
                        .paletted(fileSource(mcRl("block/budding_amethyst")))
                        .background(materialSource)
                        .stretchPaletted(true)
        );

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

    private static ResourceLocation bcRl(String path) { return rl(BuddingCrystals.MOD_ID, path); }

    private static ResourceLocation mcRl(String path) { return rl("minecraft", path); }

    private static ResourceLocation rl(String namespace, String path) { return new ResourceLocation(namespace, path); }
}
