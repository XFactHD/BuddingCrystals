package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.ConfigCondition;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingRecipeProvider extends RecipeProvider
{
    public BuddingRecipeProvider(PackOutput output)
    {
        super(output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        addBuddingCrystalRecipe(BCContent.AMETHYST, true, consumer);

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

    public static void addBuddingCrystalRecipe(CrystalSet set, boolean config, RecipeOutput consumer)
    {
        RecipeBuilder builder =  ShapedRecipeBuilder.shaped(RecipeCategory.MISC, set.getBuddingBlock())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', set.getIngredient())
                .define('C', BCContent.CRYSTAL_CATALYST.value())
                .unlockedBy("has_" + set.getName(), has(set.getIngredient()));

        wrapInConditions(set, builder, config, consumer);
    }

    private static void wrapInConditions(CrystalSet set, RecipeBuilder builder, boolean config, RecipeOutput consumer)
    {
        boolean minecraft = set.getCompatMod().equals("minecraft");
        if (!config && minecraft)
        {
            builder.save(consumer);
            return;
        }

        if (!minecraft)
        {
            consumer = consumer.withConditions(new ModLoadedCondition(set.getCompatMod()));
        }
        if (config)
        {
            consumer = consumer.withConditions(new ConfigCondition(set.getConfigString()));
        }
        builder.save(consumer);
    }
}
