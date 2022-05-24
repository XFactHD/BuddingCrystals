package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.Objects;
import java.util.function.Consumer;

public final class BuddingRecipeProvider extends RecipeProvider
{
    public BuddingRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        addBuddingCrystalRecipe("amethyst", Items.AMETHYST_SHARD, Blocks.BUDDING_AMETHYST, consumer);
        BCContent.ALL_SETS.forEach(set ->
                addBuddingCrystalRecipe(set.getName(), set.getDroppedItem(), set.getBuddingBlock(), consumer)
        );

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

    private void addBuddingCrystalRecipe(String material, Item input, Block output, Consumer<FinishedRecipe> consumer)
    {
        RecipeBuilder builder =  ShapedRecipeBuilder.shaped(output)
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', input)
                .define('C', BCContent.CRYSTAL_CATALYST.get())
                .unlockedBy("has_" + material, has(input));

        String inputNamespace = Objects.requireNonNull(input.getRegistryName()).getNamespace();
        if (inputNamespace.equals("minecraft"))
        {
            builder.save(consumer);
        }
        else
        {
            wrapInModCondition(inputNamespace, builder, consumer);
        }
    }

    private static void wrapInModCondition(String modid, RecipeBuilder builder, Consumer<FinishedRecipe> consumer)
    {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(modid))
                .addRecipe(builder::save)
                .build(consumer, RecipeBuilder.getDefaultRecipeId(builder.getResult()));
    }
}
