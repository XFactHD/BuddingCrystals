package xfacthd.buddingcrystals.common.dynpack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import dev.lukebemish.dynamicassetgenerator.api.DataResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.dynpack.generators.BlockLootTableGenerator;
import xfacthd.buddingcrystals.common.dynpack.generators.RecipeGenerator;
import xfacthd.buddingcrystals.common.util.CrystalLoader;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class DynDataPlanner
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DataResourceCache DATA_CACHE = ResourceCache.register(new DataResourceCache(BuddingCrystals.rl("data")));

    public static void init()
    {
        ResourceGenerator.register(BuddingCrystals.rl("loot_tables"), BlockLootTableGenerator.CODEC);
        ResourceGenerator.register(BuddingCrystals.rl("recipes"), RecipeGenerator.CODEC);
    }

    public static void plan(CrystalSet set)
    {
        DATA_CACHE.planSource(new BlockLootTableGenerator(set));
        DATA_CACHE.planSource(RecipeGenerator.of(set));

        planTag(BlockTags.CRYSTAL_SOUND_BLOCKS, set.getBuddingBlockHolder());
        planTag(Tags.Blocks.BUDDING_BLOCKS, set.getBuddingBlockHolder());
        planTag(Tags.Blocks.BUDS, set.getSmallBudHolder(), set.getMediumBudHolder(), set.getLargeBudHolder());
        planTag(Tags.Blocks.CLUSTERS, set.getClusterHolder());
        planTag(
                BlockTags.MINEABLE_WITH_PICKAXE,
                set.getSmallBudHolder(),
                set.getMediumBudHolder(),
                set.getLargeBudHolder(),
                set.getClusterHolder(),
                set.getBuddingBlockHolder()
        );
    }

    @SafeVarargs
    private static void planTag(TagKey<Block> tag, Holder<Block>... block)
    {
        Set<ResourceLocation> entries = Arrays.stream(block)
                .map(Holder::unwrapKey)
                .map(Optional::orElseThrow)
                .map(ResourceKey::location)
                .collect(Collectors.toSet());
        DATA_CACHE.tags().queue(tag.location().withPrefix("block/"), entries);
    }

    public static void onAddDataPackFinders(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.SERVER_DATA)
        {
            // Abuse datapack pack finder collection to reload server data of crystal definitions.
            // This event fires every time a datapack reload is started

            LOGGER.info("Reloading crystal definitions for dynamic resources reload");
            Stopwatch stopwatch = Stopwatch.createStarted();
            CrystalLoader.updateFromJson(CrystalLoader.Update.SERVER);
            stopwatch.stop();
            LOGGER.info("Reloaded crystal definitions in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private DynDataPlanner() { }
}
