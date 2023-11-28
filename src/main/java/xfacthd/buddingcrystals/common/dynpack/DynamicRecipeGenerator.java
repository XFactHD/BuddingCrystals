package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingRecipeProvider;

import java.util.*;
import java.util.concurrent.CompletableFuture;

final class DynamicRecipeGenerator extends RecipeProvider
{
    private final Map<ResourceLocation, String> cache;

    public DynamicRecipeGenerator(Map<ResourceLocation, String> cache, CompletableFuture<HolderLookup.Provider> holderProvider)
    {
        super(DummyPackOutput.INSTANCE, holderProvider);
        this.cache = cache;
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        BCContent.loadedSets().forEach(set ->
                BuddingRecipeProvider.addBuddingCrystalRecipe(set, false, consumer)
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        Set<ResourceLocation> built = new HashSet<>();

        lookupProvider.thenAccept(provider -> buildRecipes(new RecipeOutput()
        {
            @Override
            @SuppressWarnings("removal")
            public Advancement.Builder advancement()
            {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void accept(FinishedRecipe recipe, ICondition... conditions)
            {
                if (!built.add(recipe.id()))
                {
                    throw new IllegalStateException("Duplicate recipe " + recipe.id());
                }

                JsonObject serializedRecipe = recipe.serializeRecipe();
                ICondition.writeConditions(provider, serializedRecipe, conditions);
                DynamicRecipeGenerator.this.cache.put(
                        BuddingPackResources.bcRl("recipes/" + recipe.id().getPath() + ".json"),
                        serializedRecipe.toString()
                );

                AdvancementHolder advancementholder = recipe.advancement();
                if (advancementholder != null)
                {
                    JsonObject serializedAdvancement = advancementholder.value().serializeToJson();
                    ICondition.writeConditions(provider, serializedAdvancement, conditions);
                    DynamicRecipeGenerator.this.cache.put(
                            BuddingPackResources.bcRl("advancements/" + advancementholder.id().getPath() + ".json"),
                            serializedAdvancement.toString()
                    );
                }
            }
        })).join();
        return CompletableFuture.completedFuture(null);
    }
}
