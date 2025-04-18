package xfacthd.buddingcrystals.common.dynpack.generators;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.Nullable;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.data.UnownedReferenceHolder;
import xfacthd.buddingcrystals.common.dynpack.DynPackUtils;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class BlockLootTableGenerator implements ResourceGenerator
{
    public static final MapCodec<BlockLootTableGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(gen -> gen.name),
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("dropped_item").forGetter(gen -> gen.dropItem),
            Codec.FLOAT.fieldOf("normal_drops").forGetter(gen -> gen.normalDrops),
            Codec.FLOAT.fieldOf("max_drops").forGetter(gen -> gen.maxDrops)
    ).apply(inst, BlockLootTableGenerator::new));
    private static final String FILE_PREFIX = "loot_table/blocks/";
    /** Special registry ops with a dummy enchantment registry to satisfy holder(set) serialization */
    private static final RegistryOps<JsonElement> SPECIAL_JSON_OPS = RegistryOps.create(
            JsonOps.INSTANCE,
            new RegistryAccess.ImmutableRegistryAccess(Map.of(
                    Registries.ENCHANTMENT,
                    new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.experimental()))
            )
    );

    private final String name;
    private final Holder<Item> dropItem;
    private final float normalDrops;
    private final float maxDrops;
    private final Map<ResourceLocation, Supplier<LootTable>> assignedTables;

    public BlockLootTableGenerator(CrystalSet set)
    {
        this(set.getName(), set.getDroppedItemHolder(), set.getNormalDrops(), set.getMaxDrops());
    }

    private BlockLootTableGenerator(String name, Holder<Item> dropItem, float normalDrops, float maxDrops)
    {
        this.name = name;
        this.dropItem = dropItem;
        this.normalDrops = normalDrops;
        this.maxDrops = maxDrops;
        ResourceLocation smallBudLoc = BuddingCrystals.rl(CrystalSet.smallBudId(name));
        ResourceLocation mediumBudLoc = BuddingCrystals.rl(CrystalSet.mediumBudId(name));
        ResourceLocation largeBudLoc = BuddingCrystals.rl(CrystalSet.largeBudId(name));
        ResourceLocation clusterLoc = BuddingCrystals.rl(CrystalSet.clusterId(name));
        ResourceLocation buddingBlockLoc = BuddingCrystals.rl(CrystalSet.buddingBlockId(name));
        this.assignedTables = Map.of(
                toFileLoc(buddingBlockLoc), () -> LootTable.lootTable().setRandomSequence(buddingBlockLoc).build(),
                toFileLoc(smallBudLoc), () -> buildBudTable(smallBudLoc),
                toFileLoc(mediumBudLoc), () -> buildBudTable(mediumBudLoc),
                toFileLoc(largeBudLoc), () -> buildBudTable(largeBudLoc),
                toFileLoc(clusterLoc), () -> buildClusterTable(clusterLoc, dropItem, normalDrops, maxDrops)
        );
    }

    /** Copy of {@link BlockLootSubProvider#dropWhenSilkTouch(Block)} */
    private static LootTable buildBudTable(ResourceLocation block)
    {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(createSilkTouchCondition())
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(BuiltInRegistries.ITEM.get(block)))
                )
                .setRandomSequence(block)
                .build();
    }

    /** Copy of {@link BlockLootSubProvider#createSilkTouchDispatchTable(Block, LootPoolEntryContainer.Builder)} and the loot builder of {@link Blocks#AMETHYST_CLUSTER} */
    private static LootTable buildClusterTable(ResourceLocation block, Holder<Item> dropItem, float normalDrops, float maxDrops)
    {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(BuiltInRegistries.ITEM.get(block))
                                        .when(createSilkTouchCondition())
                                        .otherwise(LootItem.lootTableItem(dropItem.value())
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(maxDrops)))
                                                .apply(ApplyBonusCount.addOreBonusCount(new UnownedReferenceHolder<>(Enchantments.FORTUNE)))
                                                .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                                                .otherwise(
                                                        LootItem.lootTableItem(dropItem.value())
                                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(normalDrops)))
                                                                .apply(ApplyExplosionDecay.explosionDecay())
                                                )))
                )
                .build();
    }

    /** Copy of {@link BlockLootSubProvider#hasSilkTouch()} */
    private static LootItemCondition.Builder createSilkTouchCondition()
    {
        return MatchTool.toolMatches(
                ItemPredicate.Builder.item()
                        .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(
                                        List.of(new EnchantmentPredicate(
                                                new UnownedReferenceHolder<>(Enchantments.SILK_TOUCH),
                                                MinMaxBounds.Ints.atLeast(1)
                                        ))
                                )
                        )
        );
    }

    private static ResourceLocation toFileLoc(ResourceLocation id)
    {
        return id.withPrefix(FILE_PREFIX).withSuffix(".json");
    }

    @Override
    public Set<ResourceLocation> getLocations(ResourceGenerationContext ctx)
    {
        return assignedTables.keySet();
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> get(ResourceLocation loc, ResourceGenerationContext ctx)
    {
        Supplier<LootTable> supplier = assignedTables.get(loc);
        if (supplier != null)
        {
            String lootTable = DynPackUtils.toJson(LootTable.CODEC, SPECIAL_JSON_OPS, Holder.direct(supplier.get()));
            return lootTable != null ? DynPackUtils.toIoSupplier(lootTable) : null;
        }
        return null;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public <T> DataResult<T> persistentCacheData(DynamicOps<T> ops, ResourceLocation loc, ResourceGenerationContext ctx)
    {
        return DataResult.success(ops.empty());
    }

    @Override
    public MapCodec<? extends ResourceGenerator> codec()
    {
        return CODEC;
    }
}
