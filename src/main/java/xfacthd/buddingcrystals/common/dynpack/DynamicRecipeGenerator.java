package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.JsonObject;
import net.minecraft.DetectedVersion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingRecipeProvider;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

final class DynamicRecipeGenerator extends RecipeProvider
{
    private final Map<ResourceLocation, String> cache;

    public DynamicRecipeGenerator(Map<ResourceLocation, String> cache)
    {
        super(new DataGenerator(Path.of(""), List.of(), DetectedVersion.tryDetectVersion(), true));
        this.cache = cache;
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        BCContent.loadedSets().forEach(set ->
                BuddingRecipeProvider.addBuddingCrystalRecipe(set, false, consumer)
        );
    }

    @Override
    public void run(CachedOutput cache)
    {
        Set<ResourceLocation> built = new HashSet<>();

        buildCraftingRecipes(recipe ->
        {
            if (!built.add(recipe.getId()))
            {
                throw new IllegalStateException("Duplicate recipe " + recipe.getId());
            }

            ResourceLocation recipePath = BuddingPackResources.bcRl("recipes/" + recipe.getId().getPath() + ".json");
            this.cache.put(recipePath, recipe.serializeRecipe().toString());

            JsonObject advancement = recipe.serializeAdvancement();
            if (advancement != null)
            {
                //noinspection ConstantConditions
                ResourceLocation advancementPath = BuddingPackResources.bcRl("advancements/" + recipe.getAdvancementId().getPath() + ".json");
                this.cache.put(advancementPath, advancement.toString());
            }
        });
    }
}
