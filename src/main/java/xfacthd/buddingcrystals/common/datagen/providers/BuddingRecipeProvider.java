package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.ConfigCondition;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.concurrent.CompletableFuture;

public final class BuddingRecipeProvider extends RecipeProvider
{
    public BuddingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BCContent.AMETHYST.getBuddingBlock())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', BCContent.AMETHYST.getIngredient())
                .define('C', BCContent.CRYSTAL_CATALYST.value())
                .unlockedBy("has_" + BCContent.AMETHYST.getName(), has(BCContent.AMETHYST.getIngredient()))
                .save(
                        consumer.withConditions(new ConfigCondition(BCContent.AMETHYST.getConfigString())),
                        BuddingCrystals.rl(CrystalSet.buddingBlockId(BCContent.AMETHYST.getName()))
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BCContent.CRYSTAL_CATALYST.value())
                .pattern("RBR")
                .pattern("BAB")
                .pattern("RBR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('B', Items.BLAZE_POWDER)
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy("hasAmethyst", has(Items.AMETHYST_SHARD))
                .save(consumer);
    }
}
