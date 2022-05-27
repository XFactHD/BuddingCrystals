package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
    private final String translation;
    private final ResourceLocation texture;
    private final RegistryObject<Block> buddingBlock;
    private final BudSet budSet;
    private final RegistryObject<Item> drop;
    private final float normalDrop;
    private final float maxDrop;

    private CrystalSet(String compatMod, String name, String translation, ResourceLocation texture, RegistryObject<Block> buddingBlock, BudSet budSet, RegistryObject<Item> drop, float normalDrop, float maxDrop)
    {
        this.compatMod = compatMod;
        this.name = name;
        this.translation = translation;
        this.texture = texture;
        this.buddingBlock = buddingBlock;
        this.budSet = budSet;
        this.drop = drop;
        this.normalDrop = normalDrop;
        this.maxDrop = maxDrop;
    }

    public String getName() { return name; }

    public String getTranslation() { return translation; }

    public ResourceLocation getSourceTexture() { return texture; }

    public Block getBuddingBlock() { return buddingBlock.get(); }

    public Block getSmallBud() { return budSet.smallBud.get(); }

    public Block getMediumBud() { return budSet.mediumBud.get(); }

    public Block getLargeBud() { return budSet.largeBud.get(); }

    public Block getCluster() { return budSet.cluster.get(); }

    public BudSet getBudSet() { return budSet; }

    public Item getDroppedItem() { return drop.get(); }

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



    public static CrystalSet.Builder builder(String name) { return new Builder(name); }

    public static CrystalSet builtinAmethyst()
    {
        BudSet budSet = new BudSet(
                RegistryObject.create(new ResourceLocation("small_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("medium_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("large_amethyst_bud"), ForgeRegistries.BLOCKS),
                RegistryObject.create(new ResourceLocation("amethyst_cluster"), ForgeRegistries.BLOCKS)
        );

        return new CrystalSet(
                "minecraft",
                "amethyst",
                "Amethyst",
                new ResourceLocation("minecraft:item/amethyst_shard"),
                RegistryObject.create(new ResourceLocation("budding_amethyst"), ForgeRegistries.BLOCKS),
                budSet,
                RegistryObject.create(new ResourceLocation("amethyst_shard"), ForgeRegistries.ITEMS),
                2,
                4
        );
    }



    public static final class Builder
    {
        private final String name;
        private String translation;
        private String compatMod = "minecraft";
        private String texture;
        private int growthChance = 5;
        private RegistryObject<Item> drop;
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
            this.texture = texture;
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
            this.drop = RegistryObject.create(new ResourceLocation(drop), ForgeRegistries.ITEMS);
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
            Preconditions.checkState(translation != null, "No translation set");
            Preconditions.checkState(texture != null, "No texture set");
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

            CrystalSet set = new CrystalSet(
                    compatMod,
                    name,
                    translation,
                    new ResourceLocation(compatMod, texture),
                    buddingBlock,
                    budSet,
                    drop,
                    normalDrop,
                    maxDrop
            );
            BCContent.ALL_SETS.add(set);
            return set;
        }



        private static AmethystClusterBlock smallBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 1, 3, 4); }

        private static AmethystClusterBlock mediumBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 2, 4, 3); }

        private static AmethystClusterBlock largeBud() { return cluster(SoundType.SMALL_AMETHYST_BUD, 4, 5, 3); }

        private static AmethystClusterBlock cluster() { return cluster(SoundType.AMETHYST_CLUSTER, 5, 7, 3); }

        private static AmethystClusterBlock cluster(SoundType sound, int light, int height, int widthShrink)
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

        private static RegistryObject<Block> register(String name, Supplier<Block> blockFactory, String compatMod)
        {
            RegistryObject<Block> block = BCContent.BLOCKS.register(name, blockFactory);
            BCContent.ITEMS.register(name, () -> CrystalBlockItem.make(block.get(), compatMod));
            return block;
        }
    }
}
