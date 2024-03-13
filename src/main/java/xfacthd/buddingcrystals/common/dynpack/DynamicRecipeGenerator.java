package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingRecipeProvider;

import java.util.*;
import java.util.concurrent.CompletableFuture;

final class DynamicRecipeGenerator extends RecipeProvider
{
    private final Map<ResourceLocation, String> cache;

    public DynamicRecipeGenerator(Map<ResourceLocation, String> cache)
    {
        super(DummyPackOutput.INSTANCE);
        this.cache = cache;
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        BCContent.allActiveSets().forEach(set ->
                BuddingRecipeProvider.addBuddingCrystalRecipe(set, false, consumer)
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        Set<ResourceLocation> built = new HashSet<>();

        buildRecipes(new RecipeOutput()
        {
            @Override
            @SuppressWarnings("removal")
            public Advancement.Builder advancement()
            {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement, ICondition... conditions)
            {
                if (!built.add(id))
                {
                    throw new IllegalStateException("Duplicate recipe " + id);
                }

                JsonElement serializedRecipe = Util.getOrThrow(Recipe.CONDITIONAL_CODEC.encodeStart(
                        JsonOps.INSTANCE, Optional.of(new WithConditions<>(recipe, conditions))
                ), IllegalStateException::new);
                DynamicRecipeGenerator.this.cache.put(
                        BuddingPackResources.bcRl("recipes/" + id.getPath() + ".json"),
                        serializedRecipe.toString()
                );

                if (advancement != null)
                {
                    JsonElement serializedAdvancement = Util.getOrThrow(Advancement.CONDITIONAL_CODEC.encodeStart(
                            JsonOps.INSTANCE, Optional.of(new WithConditions<>(advancement.value(), conditions))
                    ), IllegalStateException::new);
                    DynamicRecipeGenerator.this.cache.put(
                            BuddingPackResources.bcRl("advancements/" + id.getPath() + ".json"),
                            serializedAdvancement.toString()
                    );
                }
            }
        });
        return CompletableFuture.completedFuture(null);
    }
}
