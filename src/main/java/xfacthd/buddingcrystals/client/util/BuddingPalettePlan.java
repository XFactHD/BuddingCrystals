package xfacthd.buddingcrystals.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.lukebemish.dynamic_asset_generator.client.api.ClientPrePackRepository;
import io.github.lukebemish.dynamic_asset_generator.client.api.DynAssetGeneratorClientAPI;
import io.github.lukebemish.dynamic_asset_generator.client.util.IPalettePlan;
import net.minecraft.resources.ResourceLocation;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.io.IOException;

public final class BuddingPalettePlan implements IPalettePlan
{
    private static final ResourceLocation FALLBACK_TEXTURE = bcRl("textures/block/fallback.png");
    private static final ResourceLocation EMPTY_TEXTURE = bcRl("textures/block/empty.png");

    private final ResourceLocation background;
    private final ResourceLocation paletted;

    private BuddingPalettePlan(ResourceLocation background, ResourceLocation paletted)
    {
        this.background = new ResourceLocation(background.getNamespace(), "textures/" + background.getPath() + ".png");
        this.paletted = new ResourceLocation(paletted.getNamespace(), "textures/" + paletted.getPath() + ".png");
    }

    @Override
    public NativeImage getBackground() throws IOException { return readImage(background); }

    @Override
    public NativeImage getOverlay() throws IOException { return readImage(EMPTY_TEXTURE); }

    @Override
    public NativeImage getPaletted() throws IOException { return readImage(paletted); }

    @Override
    public boolean includeBackground() { return false; }

    @Override
    public boolean stretchPaletted() { return true; }

    @Override
    public int extend() { return 0; }



    public static void plan(CrystalSet set)
    {
        String name = set.getName();

        plan("block/budding/" + name, new BuddingPalettePlan(
                set.isActive() ? set.getBuddingSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/budding_amethyst")
        ));

        plan("block/small_bud/" + name, new BuddingPalettePlan(
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/small_amethyst_bud")
        ));
        plan("block/medium_bud/" + name, new BuddingPalettePlan(
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/medium_amethyst_bud")
        ));
        plan("block/large_bud/" + name, new BuddingPalettePlan(
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/large_amethyst_bud")
        ));
        plan("block/cluster/" + name, new BuddingPalettePlan(
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                mcRl("block/amethyst_cluster")
        ));
    }

    public static void planCatalyst()
    {
        plan("item/crystal_catalyst", new BuddingPalettePlan(
                mcRl("item/amethyst_shard"),
                mcRl("item/blaze_powder")
        ));
    }

    private static void plan(String path, BuddingPalettePlan plan)
    {
        DynAssetGeneratorClientAPI.planPaletteCombinedImage(
                bcRl("textures/" + path + ".png"),
                plan
        );
    }

    private static NativeImage readImage(ResourceLocation location) throws IOException
    {
        return NativeImage.read(ClientPrePackRepository.getResource(location));
    }

    private static ResourceLocation bcRl(String path) { return rl(BuddingCrystals.MOD_ID, path); }

    private static ResourceLocation mcRl(String path) { return rl("minecraft", path); }

    private static ResourceLocation rl(String namespace, String path) { return new ResourceLocation(namespace, path); }
}
