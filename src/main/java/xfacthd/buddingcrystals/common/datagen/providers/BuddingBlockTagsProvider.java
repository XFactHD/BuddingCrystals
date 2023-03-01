package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

public final class BuddingBlockTagsProvider extends TagsProvider<Block>
{
    public BuddingBlockTagsProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        //noinspection deprecation
        super(generator, Registry.BLOCK, BuddingCrystals.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags()
    {
        TagAppender<Block> crystalSoundBlocks = tag(BlockTags.CRYSTAL_SOUND_BLOCKS);
        TagAppender<Block> buddingBlocks = tag(BCContent.BUDDING_BLOCKS_TAG);
        TagAppender<Block> pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        BCContent.builtinSets().forEach(set ->
        {
            crystalSoundBlocks.add(set.getBuddingBlock());
            buddingBlocks.add(set.getBuddingBlock());
            pickaxeMineable.add(set.blocks().toArray(Block[]::new));
        });
    }

    @Override
    public String getName() { return "block tags"; }
}
