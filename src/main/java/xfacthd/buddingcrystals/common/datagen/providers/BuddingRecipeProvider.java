package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.ConfigCondition;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.function.Consumer;

public final class BuddingRecipeProvider extends RecipeProvider
{
    public BuddingRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        addBuddingCrystalRecipe(BCContent.AMETHYST, consumer);
        BCContent.ALL_SETS.forEach(set -> addBuddingCrystalRecipe(set, consumer));

        ShapedRecipeBuilder.shaped(BCContent.CRYSTAL_CATALYST.get())
                .pattern("RBR")
                .pattern("BAB")
                .pattern("RBR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('B', Items.BLAZE_POWDER)
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy("hasAmethyst", has(Items.AMETHYST_SHARD))
                .save(consumer);
    }

    private void addBuddingCrystalRecipe(CrystalSet set, Consumer<FinishedRecipe> consumer)
    {
        RecipeBuilder builder =  ShapedRecipeBuilder.shaped(set.getBuddingBlock())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', set.getDroppedItem())
                .define('C', BCContent.CRYSTAL_CATALYST.get())
                .unlockedBy("has_" + set.getName(), has(set.getDroppedItem()));

        wrapInConditions(set, builder, consumer);
    }

    private static void wrapInConditions(CrystalSet set, RecipeBuilder builder, Consumer<FinishedRecipe> consumer)
    {
        ConditionalRecipe.Builder conditionalBuilder = ConditionalRecipe.builder();

        if (!set.getCompatMod().equals("minecraft"))
        {
            ICondition modCondition = new ModLoadedCondition(set.getCompatMod());
            conditionalBuilder.addCondition(modCondition);
        }

        FinishedRecipe[] recipe = new FinishedRecipe[1];
        builder.save(finishedRecipe -> recipe[0] = finishedRecipe);

        conditionalBuilder.addCondition(new ConfigCondition(set.getConfigString()))
                .addRecipe(recipe[0])
                .generateAdvancement(recipe[0].getAdvancementId())
                .build(consumer, RecipeBuilder.getDefaultRecipeId(builder.getResult()));
    }
}
