package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.Block;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.Map;

@SuppressWarnings("deprecation")
public final class DynamicTagGenerator extends TagsProvider<Block>
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final Map<ResourceLocation, String> cache;

    DynamicTagGenerator(Map<ResourceLocation, String> cache)
    {
        //noinspection ConstantConditions
        super(null, Registry.BLOCK, BuddingCrystals.MOD_ID, null);
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
    protected Tag.Builder getOrCreateRawBuilder(TagKey<Block> tag)
    {
        return builders.computeIfAbsent(tag.location(), loc -> new Tag.Builder());
    }

    @Override
    public void run(HashCache cache)
    {
        builders.clear();
        addTags();
        builders.forEach((loc, tag) ->
        {
            ResourceKey<? extends Registry<Block>> resourcekey = registry.key();

            this.cache.put(
                    new ResourceLocation(loc.getNamespace(), TagManager.getTagDir(resourcekey) + "/" + loc.getPath() + ".json"),
                    GSON.toJson(tag.serializeToJson())
            );
        });
    }

    @Override
    public String getName() { return "dynamic_tags"; }
}
