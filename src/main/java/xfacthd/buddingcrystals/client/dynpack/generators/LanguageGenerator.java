package xfacthd.buddingcrystals.client.dynpack.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import dev.lukebemish.dynamicassetgenerator.api.Resettable;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;
import org.jetbrains.annotations.Nullable;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.dynpack.DynAssetPlanner;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.DynPackUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LanguageGenerator implements ResourceGenerator, Resettable
{
    public static final MapCodec<LanguageGenerator> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
            .fieldOf("entries")
            .xmap(LanguageGenerator::fromCache, LanguageGenerator::getResolvedEntries);
    private static final ResourceLocation PATH = BuddingCrystals.rl("lang/en_us.json");
    private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().setPrettyPrinting().create();

    private final Map<String, Supplier<String>> entries;
    @Nullable
    private Map<String, String> resolvedEntries;

    public static LanguageGenerator fromDefinitions()
    {
        return new LanguageGenerator(BCContent.allActiveSets().stream().flatMap(set -> Stream.<Pair<String, Supplier<String>>>of(
                Pair.of(key(set.getSmallBudHolder()), () -> "Small " + set.getTranslation() + " Bud"),
                Pair.of(key(set.getMediumBudHolder()), () -> "Medium " + set.getTranslation() + " Bud"),
                Pair.of(key(set.getLargeBudHolder()), () -> "Large " + set.getTranslation() + " Bud"),
                Pair.of(key(set.getClusterHolder()), () -> set.getTranslation() + " Cluster"),
                Pair.of(key(set.getBuddingBlockHolder()), () -> "Budding " + set.getTranslation())
        )).collect(Pair.toMap()), null);
    }

    private static LanguageGenerator fromCache(Map<String, String> resolvedEntries)
    {
        Map<String, Supplier<String>> entries = new HashMap<>(resolvedEntries.size());
        resolvedEntries.forEach((key, translation) -> entries.put(key, () -> translation));
        return new LanguageGenerator(entries, resolvedEntries);
    }

    private LanguageGenerator(Map<String, Supplier<String>> entries, @Nullable Map<String, String> resolvedEntries)
    {
        this.entries = entries;
        this.resolvedEntries = resolvedEntries;
    }

    private static String key(Holder<Block> block)
    {
        return Util.makeDescriptionId("block", block.unwrapKey().orElseThrow().location());
    }

    @Override
    public Set<ResourceLocation> getLocations(ResourceGenerationContext context)
    {
        return Set.of(PATH);
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public IoSupplier<InputStream> get(ResourceLocation outRl, ResourceGenerationContext context)
    {
        if (outRl.equals(PATH))
        {
            // Escape Unicode after the fact so that it's not double escaped by GSON
            String data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(GSON.toJson(getResolvedEntries()));
            return DynPackUtils.toIoSupplier(data);
        }
        return null;
    }

    private synchronized Map<String, String> getResolvedEntries()
    {
        if (resolvedEntries == null)
        {
            DynAssetPlanner.ensureCrystalDataReloaded();
            resolvedEntries = new HashMap<>(entries.size());
            entries.forEach((key, supplier) -> resolvedEntries.put(key, supplier.get()));
        }
        return resolvedEntries;
    }

    @Override
    public void reset(ResourceGenerationContext context)
    {
        resolvedEntries = null;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public <T> DataResult<T> persistentCacheData(DynamicOps<T> ops, ResourceLocation location, ResourceGenerationContext context)
    {
        return DataResult.success(ops.empty());
    }

    @Override
    public MapCodec<? extends ResourceGenerator> codec()
    {
        return CODEC;
    }
}
