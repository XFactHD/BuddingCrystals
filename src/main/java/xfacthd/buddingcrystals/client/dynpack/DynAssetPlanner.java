package xfacthd.buddingcrystals.client.dynpack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TextureGenerator;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.PaletteCombinedSource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReaderSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.dynpack.generators.BlockStateGenerator;
import xfacthd.buddingcrystals.client.dynpack.generators.LanguageGenerator;
import xfacthd.buddingcrystals.common.util.CrystalLoader;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DynAssetPlanner
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AssetResourceCache ASSET_CACHE = ResourceCache.register(new AssetResourceCache(BuddingCrystals.rl("assets")));
    private static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("neoforge", "white");
    private static final ResourceLocation EMPTY_TEXTURE = ResourceLocation.fromNamespaceAndPath("dynamic_asset_generator", "empty");
    private static final AtomicBoolean CRYSTALS_RELOADED = new AtomicBoolean();

    public static void init()
    {
        ResourceGenerator.register(BuddingCrystals.rl("language"), LanguageGenerator.CODEC);
        ResourceGenerator.register(BuddingCrystals.rl("blockstates"), BlockStateGenerator.CODEC);
    }

    public static void plan()
    {
        ASSET_CACHE.planSource(LanguageGenerator.fromDefinitions());
    }

    public static void plan(CrystalSet set)
    {
        String name = set.getName();

        planTexture("block/budding/" + name,
                set.isActive() ? set.getBuddingSourceTexture() : FALLBACK_TEXTURE,
                ResourceLocation.withDefaultNamespace("block/budding_amethyst")
        );

        planTexture("block/small_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                ResourceLocation.withDefaultNamespace("block/small_amethyst_bud")
        );
        planTexture("block/medium_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                ResourceLocation.withDefaultNamespace("block/medium_amethyst_bud")
        );
        planTexture("block/large_bud/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                ResourceLocation.withDefaultNamespace("block/large_amethyst_bud")
        );
        planTexture("block/cluster/" + name,
                set.isActive() ? set.getCrystalSourceTexture() : FALLBACK_TEXTURE,
                ResourceLocation.withDefaultNamespace("block/amethyst_cluster")
        );

        ASSET_CACHE.planSource(new BlockStateGenerator(set));
    }

    public static void planCatalyst()
    {
        planTexture("item/crystal_catalyst", ResourceLocation.withDefaultNamespace("item/amethyst_shard"), ResourceLocation.withDefaultNamespace("item/blaze_powder"));
    }

    private static void planTexture(String path, ResourceLocation background, ResourceLocation paletted)
    {
        ASSET_CACHE.planSource(
                new TextureGenerator(
                        BuddingCrystals.rl(path),
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

    public static synchronized void ensureCrystalDataReloaded()
    {
        if (!CRYSTALS_RELOADED.get())
        {
            LOGGER.info("Reloading crystal definitions for dynamic resources reload");
            Stopwatch stopwatch = Stopwatch.createStarted();
            CrystalLoader.updateFromJson(CrystalLoader.Update.CLIENT);
            stopwatch.stop();
            LOGGER.info("Reloaded crystal definitions in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

            CRYSTALS_RELOADED.set(true);
        }
    }

    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> CRYSTALS_RELOADED.set(false));
    }



    private DynAssetPlanner() { }
}
