package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.PackOutput;
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
    public BuddingRecipeProvider(PackOutput output) { super(output); }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        addBuddingCrystalRecipe(BCContent.AMETHYST, true, consumer);
        BCContent.builtinSets().forEach(set -> addBuddingCrystalRecipe(set, true, consumer));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BCContent.CRYSTAL_CATALYST.get())
                .pattern("RBR")
                .pattern("BAB")
                .pattern("RBR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('B', Items.BLAZE_POWDER)
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy("hasAmethyst", has(Items.AMETHYST_SHARD))
                .save(consumer);
    }

    public static void addBuddingCrystalRecipe(CrystalSet set, boolean config, Consumer<FinishedRecipe> consumer)
    {
        RecipeBuilder builder =  ShapedRecipeBuilder.shaped(RecipeCategory.MISC, set.getBuddingBlock())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', set.getIngredient())
                .define('C', BCContent.CRYSTAL_CATALYST.get())
                .unlockedBy("has_" + set.getName(), has(set.getIngredient()));

        wrapInConditions(set, builder, config, consumer);
    }

    private static void wrapInConditions(CrystalSet set, RecipeBuilder builder, boolean config, Consumer<FinishedRecipe> consumer)
    {
        boolean minecraft = set.getCompatMod().equals("minecraft");
        if (!config && minecraft)
        {
            builder.save(consumer);
            return;
        }

        ConditionalRecipe.Builder conditionalBuilder = ConditionalRecipe.builder();

        if (!minecraft)
        {
            ICondition modCondition = new ModLoadedCondition(set.getCompatMod());
            conditionalBuilder.addCondition(modCondition);
        }
        if (config)
        {
            ICondition cfgCondition = new ConfigCondition(set.getConfigString());
            conditionalBuilder.addCondition(cfgCondition);
        }

        FinishedRecipe[] recipe = new FinishedRecipe[1];
        builder.save(finishedRecipe -> recipe[0] = finishedRecipe);

        conditionalBuilder.addRecipe(recipe[0])
                .generateAdvancement(recipe[0].getAdvancementId())
                .build(consumer, RecipeBuilder.getDefaultRecipeId(builder.getResult()));
    }
}
