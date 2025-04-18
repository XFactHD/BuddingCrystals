package xfacthd.buddingcrystals.common.dynpack.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jetbrains.annotations.Nullable;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.DynPackUtils;
import xfacthd.buddingcrystals.common.util.ConfigCondition;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class RecipeGenerator implements ResourceGenerator
{
    public static final MapCodec<RecipeGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(gen -> gen.name),
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("ingredient").forGetter(gen -> gen.ingredientItem),
            Codec.STRING.optionalFieldOf("config_condition").forGetter(gen -> Optional.ofNullable(gen.configKey)),
            Codec.STRING.optionalFieldOf("mod_loaded_condition").forGetter(gen -> Optional.ofNullable(gen.compatMod))
    ).apply(inst, (name, ing, cfg, mod) -> new RecipeGenerator(name, ing, cfg.orElse(null), mod.orElse(null))));

    private final String name;
    private final Holder<Item> ingredientItem;
    @Nullable
    private final String configKey;
    @Nullable
    private final String compatMod;
    private final ResourceLocation advancementPath;
    private final ResourceLocation recipeId;
    private final ResourceLocation recipePath;

    public static RecipeGenerator of(CrystalSet set)
    {
        String configKey = BCContent.isNotBuiltin(set.getName()) ? null : set.getConfigString();
        String compatMod = set.getCompatMod().equals("minecraft") ? null : set.getCompatMod();
        return new RecipeGenerator(set.getName(), set.getIngredientHolder(), configKey, compatMod);
    }

    private RecipeGenerator(String name, Holder<Item> ingredientItem, @Nullable String configKey, @Nullable String compatMod)
    {
        this.name = name;
        this.ingredientItem = ingredientItem;
        this.configKey = configKey;
        this.compatMod = compatMod;
        this.advancementPath = BuddingCrystals.rl("advancement/recipes/misc/" + CrystalSet.buddingBlockId(name) + ".json");
        this.recipeId = BuddingCrystals.rl(CrystalSet.buddingBlockId(name));
        this.recipePath = recipeId.withPrefix("recipe/").withSuffix(".json");
    }

    @Override
    public Set<ResourceLocation> getLocations(ResourceGenerationContext context)
    {
        return Set.of(advancementPath, recipePath);
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> get(ResourceLocation outRl, ResourceGenerationContext context)
    {
        String output = null;
        if (outRl.equals(advancementPath))
        {
            output = buildRecipe(false);
        }
        else if (outRl.equals(recipePath))
        {
            output = buildRecipe(true);
        }
        return output != null ? DynPackUtils.toIoSupplier(output) : null;
    }

    @Nullable
    private String buildRecipe(boolean requestRecipe)
    {
        String[] result = new String[1];
        RecipeOutput output = new RecipeOutput()
        {
            @Override
            @SuppressWarnings("removal")
            public Advancement.Builder advancement()
            {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions)
            {
                if (requestRecipe)
                {
                    result[0] = DynPackUtils.toConditionalJson(Recipe.CONDITIONAL_CODEC, recipe, conditions);
                }
                else if (advancement != null)
                {
                    result[0] = DynPackUtils.toConditionalJson(Advancement.CONDITIONAL_CODEC, advancement.value(), conditions);
                }
            }
        };
        if (configKey != null || compatMod != null)
        {
            List<ICondition> conditions = new ArrayList<>();
            if (configKey != null)
            {
                conditions.add(new ConfigCondition(configKey));
            }
            if (compatMod != null)
            {
                conditions.add(new ModLoadedCondition(compatMod));
            }
            output = output.withConditions(conditions.toArray(ICondition[]::new));
        }
        Item resultItem = BuiltInRegistries.ITEM.get(BuddingCrystals.rl(CrystalSet.buddingBlockId(name)));
        new ShapedRecipeBuilder(RecipeCategory.MISC, new ItemStack(resultItem))
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', ingredientItem.value())
                .define('C', BCContent.CRYSTAL_CATALYST.value())
                .unlockedBy("has", RecipeProvider.has(ingredientItem.value()))
                .save(output, recipeId);
        return result[0];
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public <T> DataResult<T> persistentCacheData(DynamicOps<T> ops, ResourceLocation location, ResourceGenerationContext context)
    {
        return DataResult.success(ops.empty());
    }

    @Override
    public MapCodec<? extends ResourceGenerator> codec()
    {
        return CODEC;
    }
}
