package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.concurrent.CompletableFuture;

public final class BuddingBlockTagsProvider extends BlockTagsProvider
{
    public BuddingBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper)
    {
        super(output, lookupProvider, BuddingCrystals.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        IntrinsicTagAppender<Block> crystalSoundBlocks = tag(BlockTags.CRYSTAL_SOUND_BLOCKS);
        IntrinsicTagAppender<Block> buddingBlocks = tag(BCContent.BUDDING_BLOCKS_TAG);
        IntrinsicTagAppender<Block> pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        BCContent.builtinSets().forEach(set ->
        {
            crystalSoundBlocks.add(set.getBuddingBlock());
            buddingBlocks.add(set.getBuddingBlock());
            pickaxeMineable.add(set.blocks().toArray(Block[]::new));
        });
    }

    @Override
    public String getName()
    {
        return "block tags";
    }
}
