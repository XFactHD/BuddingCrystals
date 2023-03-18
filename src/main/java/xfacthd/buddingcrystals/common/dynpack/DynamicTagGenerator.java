package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class DynamicTagGenerator extends BlockTagsProvider
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final Map<ResourceLocation, String> cache;

    DynamicTagGenerator(Map<ResourceLocation, String> cache, CompletableFuture<HolderLookup.Provider> holderProvider)
    {
        super(DummyPackOutput.INSTANCE, holderProvider, BuddingCrystals.MOD_ID, null);
        this.cache = cache;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        IntrinsicTagAppender<Block> crystalSoundBlocks = tag(BlockTags.CRYSTAL_SOUND_BLOCKS);
        IntrinsicTagAppender<Block> buddingBlocks = tag(BCContent.BUDDING_BLOCKS_TAG);
        IntrinsicTagAppender<Block> pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        BCContent.loadedSets().forEach(set ->
        {
            crystalSoundBlocks.add(set.getBuddingBlock());
            buddingBlocks.add(set.getBuddingBlock());
            pickaxeMineable.add(set.blocks().toArray(Block[]::new));
        });
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Block> tag)
    {
        return builders.computeIfAbsent(tag.location(), loc -> new TagBuilder());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        builders.clear();
        addTags(createContentsProvider().join());
        builders.forEach((loc, tag) ->
        {
            ResourceLocation path = new ResourceLocation(
                    loc.getNamespace(),
                    TagManager.getTagDir(registryKey) + "/" + loc.getPath() + ".json"
            );
            String tagJson = GSON.toJson(TagFile.CODEC.encodeStart(
                    JsonOps.INSTANCE,
                    new TagFile(tag.build(), false)
            ).getOrThrow(false, LOGGER::error));

            this.cache.put(path, tagJson);
        });

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() { return "dynamic_tags"; }
}
