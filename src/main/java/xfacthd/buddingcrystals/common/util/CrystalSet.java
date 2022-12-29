package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.*;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.block.BuddingCrystalBlock;
import xfacthd.buddingcrystals.common.item.CrystalBlockItem;

import java.util.List;
import java.util.function.Supplier;

public final class CrystalSet
{
    private final String compatMod;
    private final String name;
    private String translation;
    private ResourceLocation crystalTexture;
    private ResourceLocation buddingTexture;
    private final int growthChance;
    private final RegistryObject<Block> buddingBlock;
    private final BudSet budSet;
    private RegistryObject<Item> drop;
    private RegistryObject<Item> ingredient;
    private float normalDrop;
    private float maxDrop;

    CrystalSet(String compatMod, String name, String translation, ResourceLocation crystalTexture, ResourceLocation buddingTexture, int growthChance, RegistryObject<Block> buddingBlock, BudSet budSet, RegistryObject<Item> drop, RegistryObject<Item> ingredient, float normalDrop, float maxDrop)
    {
        this.compatMod = compatMod;
        this.name = name;
        this.translation = translation;
        this.crystalTexture = crystalTexture;
        this.buddingTexture = buddingTexture;
        this.growthChance = growthChance;
        this.buddingBlock = buddingBlock;
        this.budSet = budSet;
        this.drop = drop;
        this.ingredient = ingredient;
        this.normalDrop = normalDrop;
        this.maxDrop = maxDrop;
    }

    public String getName() { return name; }

    public String getTranslation() { return translation; }

    public ResourceLocation getCrystalSourceTexture() { return crystalTexture; }

    public ResourceLocation getBuddingSourceTexture() { return buddingTexture; }

    public int getGrowthChance() { return growthChance; }

    public Block getBuddingBlock() { return buddingBlock.get(); }

    public Block getSmallBud() { return budSet.smallBud.get(); }

    public Block getMediumBud() { return budSet.mediumBud.get(); }

    public Block getLargeBud() { return budSet.largeBud.get(); }

    public Block getCluster() { return budSet.cluster.get(); }

    public BudSet getBudSet() { return budSet; }

    public Item getDroppedItem() { return drop.orElse(Items.AIR); }

    public Item getIngredient() { return ingredient.orElse(Items.AIR); }

    public float getNormalDrops() { return normalDrop; }

    public float getMaxDrops() { return maxDrop; }

    public List<Block> blocks()
    {
        return List.of(
                buddingBlock.get(),
                budSet.smallBud.get(),
                budSet.mediumBud.get(),
                budSet.largeBud.get(),
                budSet.cluster.get()
        );
    }

    public String getCompatMod() { return compatMod; }

    public boolean isActive() { return ModList.get().isLoaded(compatMod); }

    public String getConfigString() { return "enable_crafting_budding_" + getName(); }

    public String getConfigTranslation() { return "config." + BuddingCrystals.MOD_ID + "." + getConfigString(); }

    public void updateClientData(String translation, ResourceLocation crystalTexture, ResourceLocation buddingTexture)
    {
        this.translation = translation;
        this.crystalTexture = crystalTexture;
        this.buddingTexture = buddingTexture;
    }

    public void updateServerData(ResourceLocation dropName, ResourceLocation ingredientName, float normalDrop, float maxDrop)
    {
        this.drop = RegistryObject.create(dropName, ForgeRegistries.ITEMS);
        this.ingredient = this.drop;
        if (!dropName.equals(ingredientName))
        {
            this.ingredient = RegistryObject.create(ingredientName, ForgeRegistries.ITEMS);
        }
        this.normalDrop = normalDrop;
        this.maxDrop = maxDrop;

        validate();
    }

    public void validate()
    {
        if (!drop.isPresent())
        {
            CrystalLoader.LOGGER.error("CrystalSet '{}' references an invalid item, it will be not drop anything!", name);
        }
        if (!ingredient.isPresent())
        {
            CrystalLoader.LOGGER.error("CrystalSet '{}' references an invalid item, it will be not be craftable!", name);
        }
    }



    public static CrystalSet.Builder builder(String name) { return new Builder(name); }

    public static CrystalSet builtinAmethyst()
    {
        BudSet budSet = new BudSet(
                RegistryObject.create(new ResourceLocation("small_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("medium_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("large_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("amethyst_cluster"), ForgeRegistries.BLOCKS)
        );

        RegistryObject<Item> drop = RegistryObject.create(new ResourceLocation("amethyst_shard"), ForgeRegistries.ITEMS);
        return new CrystalSet(
                "minecraft",
                "amethyst",
                "Amethyst",
                new ResourceLocation("minecraft:item/amethyst_shard"),
                new ResourceLocation("minecraft:item/amethyst_shard"),
                5,
                RegistryObject.create(new ResourceLocation("budding_amethyst"), ForgeRegistries.BLOCKS),
                budSet,
                drop,
                drop,
                2,
                4
        );
    }



    @SuppressWarnings({ "unused", "UnusedReturnValue" })
    public static final class Builder
    {
        private final String name;
        private String translation;
        private String compatMod = "minecraft";
        private String crystalTexPath;
        private String buddingTexPath;
        private ResourceLocation crystalTexture;
        private ResourceLocation buddingTexture;
        private int growthChance = 5;
        private RegistryObject<Item> drop;
        private RegistryObject<Item> ingredient;
        private float normalDrop = 2;
        private float maxDrop = 4;

        private Builder(String name)
        {
            Preconditions.checkArgument(name != null && !name.isEmpty(), "Name must not be empty");
            this.name = name;
        }

        public Builder translation(String translation)
        {
            Preconditions.checkArgument(translation != null && !translation.isEmpty(), "Translation must not be empty");
            this.translation = translation;
            return this;
        }

        public Builder compatMod(String compatMod)
        {
            Preconditions.checkArgument(compatMod != null && !compatMod.isEmpty(), "Compat mod must not be empty");
            this.compatMod = compatMod;
            return this;
        }

        public Builder sourceTexture(String texture)
        {
            Preconditions.checkArgument(texture != null && !texture.isEmpty(), "Texture must not be empty");
            this.crystalTexPath = texture;
            this.buddingTexPath = texture;
            return this;
        }

        public Builder buddingSourceTexture(String texture)
        {
            Preconditions.checkArgument(texture != null && !texture.isEmpty(), "Budding texture must not be empty");
            this.buddingTexPath = texture;
            return this;
        }

        public Builder crystalSourceTexture(String texture)
        {
            Preconditions.checkArgument(texture != null && !texture.isEmpty(), "Crystal texture must not be empty");
            this.crystalTexPath = texture;
            return this;
        }

        public Builder sourceTexture(ResourceLocation texture)
        {
            Preconditions.checkArgument(texture != null, "Texture must not be null");
            this.buddingTexture = texture;
            this.crystalTexture = texture;
            return this;
        }

        public Builder buddingSourceTexture(ResourceLocation texture)
        {
            Preconditions.checkArgument(texture != null, "Budding texture must not be null");
            this.buddingTexture = texture;
            return this;
        }

        public Builder crystalSourceTexture(ResourceLocation texture)
        {
            Preconditions.checkArgument(texture != null, "Crystal texture must not be null");
            this.crystalTexture = texture;
            return this;
        }

        public Builder growthChance(int chance)
        {
            Preconditions.checkArgument(chance > 0, "Growth chance must be higher than 0");
            this.growthChance = chance;
            return this;
        }

        public Builder drop(String drop)
        {
            Preconditions.checkArgument(drop != null && !drop.isEmpty(), "Dropped item must not be empty");
            return drop(new ResourceLocation(drop));
        }

        public Builder drop(ResourceLocation drop)
        {
            Preconditions.checkArgument(drop != null, "Dropped item must not be null");
            this.drop = RegistryObject.create(drop, ForgeRegistries.ITEMS);
            return this;
        }

        public Builder ingredient(String ingredient)
        {
            Preconditions.checkArgument(ingredient != null && !ingredient.isEmpty(), "Ingredient item must not be empty");
            return ingredient(new ResourceLocation(ingredient));
        }

        public Builder ingredient(ResourceLocation ingredient)
        {
            Preconditions.checkArgument(ingredient != null, "Ingredient item must not be null");
            this.ingredient = RegistryObject.create(ingredient, ForgeRegistries.ITEMS);
            return this;
        }

        public Builder normalDrop(float count)
        {
            Preconditions.checkArgument(count > 0F, "Normal drop count must be higher than 0");
            this.normalDrop = count;
            return this;
        }

        public Builder maxDrop(float count)
        {
            Preconditions.checkArgument(count > 0F, "Max drop count must be higher than 0");
            this.maxDrop = count;
            return this;
        }

        public CrystalSet build()
        {
            CrystalLoader.overrideFromJson(name, this);

            Preconditions.checkState(translation != null, "No translation set");
            Preconditions.checkState(crystalTexPath != null || crystalTexture != null, "No crystal source texture set");
            Preconditions.checkState(buddingTexPath != null || buddingTexture != null, "No budding block source texture set");
            Preconditions.checkState(drop != null, "No dropped item specified");
            Preconditions.checkState(maxDrop >= normalDrop, "Max drop must be higher or equal to normal drop");

            RegistryObject<Block> smallBud = register("small_" + name + "_bud", Builder::smallBud, compatMod);
            RegistryObject<Block> mediumBud = register("medium_" + name + "_bud", Builder::mediumBud, compatMod);
            RegistryObject<Block> largeBud = register("large_" + name + "_bud", Builder::largeBud, compatMod);
            RegistryObject<Block> cluster = register(name + "_cluster", Builder::cluster, compatMod);
            BudSet budSet = new BudSet(smallBud, mediumBud, largeBud, cluster);

            RegistryObject<Block> buddingBlock = register(
                    "budding_" + name,
                    () -> new BuddingCrystalBlock(
                            budSet,
                            growthChance,
                            BlockBehaviour.Properties.of(Material.AMETHYST)
                                    .randomTicks()
                                    .strength(1.5F)
                                    .sound(SoundType.AMETHYST)
                                    .requiresCorrectToolForDrops()
                    ),
                    compatMod
            );

            if (crystalTexture == null)
            {
                crystalTexture = new ResourceLocation(compatMod, crystalTexPath);
            }
            if (buddingTexture == null)
            {
                buddingTexture = new ResourceLocation(compatMod, buddingTexPath);
            }

            CrystalSet set = new CrystalSet(
                    compatMod,
                    name,
                    translation,
                    crystalTexture,
                    buddingTexture,
                    growthChance,
                    buddingBlock,
                    budSet,
                    drop,
                    ingredient == null ? drop : ingredient,
                    normalDrop,
                    maxDrop
            );
            BCContent.ALL_SETS.put(name, set);
            BCContent.BUILTIN_SETS.put(name, set);
            return set;
        }



        static AmethystClusterBlock smallBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 1, 3, 4); }

        static AmethystClusterBlock mediumBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 2, 4, 3); }

        static AmethystClusterBlock largeBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 4, 5, 3); }

        static AmethystClusterBlock cluster() { return cluster(SoundType.AMETHYST_CLUSTER, 5, 7, 3); }

        static AmethystClusterBlock cluster(SoundType sound, int light, int height, int widthShrink)
        {
            BlockBehaviour.Properties props = BlockBehaviour.Properties
                    .of(Material.AMETHYST)
                    .noOcclusion()
                    .randomTicks()
                    .sound(sound)
                    .strength(1.5F)
                    .lightLevel(state -> light);

            return new AmethystClusterBlock(height, widthShrink, props);
        }

        static RegistryObject<Block> register(String name, Supplier<Block> blockFactory, String compatMod)
        {
            RegistryObject<Block> block = BCContent.BLOCKS.register(name, blockFactory);
            BCContent.ITEMS.register(name, () -> new CrystalBlockItem(block.get(), compatMod));
            return block;
        }
    }
}
