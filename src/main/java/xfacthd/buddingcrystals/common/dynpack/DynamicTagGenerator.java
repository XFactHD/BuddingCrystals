package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.DetectedVersion;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class DynamicTagGenerator extends TagsProvider<Block>
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final Map<ResourceLocation, String> cache;

    DynamicTagGenerator(Map<ResourceLocation, String> cache)
    {
        super(new DataGenerator(Path.of(""), List.of(), DetectedVersion.tryDetectVersion(), true), Registry.BLOCK, BuddingCrystals.MOD_ID, null);
        this.cache = cache;
    }

    @Override
    protected void addTags()
    {
        TagAppender<Block> crystalSoundBlocks = tag(BlockTags.CRYSTAL_SOUND_BLOCKS);
        TagAppender<Block> pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        BCContent.loadedSets().forEach(set ->
        {
            crystalSoundBlocks.add(set.getBuddingBlock());
            pickaxeMineable.add(set.blocks().toArray(Block[]::new));
        });
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Block> tag)
    {
        return builders.computeIfAbsent(tag.location(), loc -> new TagBuilder());
    }

    @Override
    public void run(CachedOutput cache)
    {
        builders.clear();
        addTags();
        builders.forEach((loc, tag) ->
        {
            ResourceKey<? extends Registry<Block>> resourcekey = registry.key();

            List<TagEntry> list = tag.build();
            this.cache.put(
                    new ResourceLocation(loc.getNamespace(), TagManager.getTagDir(resourcekey) + "/" + loc.getPath() + ".json"),
                    GSON.toJson(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, false)).getOrThrow(false, LOGGER::error))
            );
        });
    }

    @Override
    public String getName() { return "dynamic_tags"; }
}
