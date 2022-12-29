package xfacthd.buddingcrystals.common.dynpack;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.data.OptionalLootItem;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.*;
import java.util.function.BiConsumer;

final class DynamicBlockLoot extends BlockLootSubProvider
{
    private static final FeatureFlagSet ENABLED_FEATURES = FeatureFlags.VANILLA_SET;

    public DynamicBlockLoot()
    {
        super(Set.of(), ENABLED_FEATURES);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) { }

    public void run(Map<ResourceLocation, String> cache)
    {
        generate();

        Map<ResourceLocation, LootTable.Builder> tables = ObfuscationReflectionHelper.getPrivateValue(BlockLootSubProvider.class, this, "f_244441_");
        Map<ResourceLocation, LootTable> built = new HashMap<>();

        Objects.requireNonNull(tables).forEach((loc, builder) ->
        {
            if (!built.containsKey(loc))
            {
                built.put(loc, builder.setParamSet(LootContextParamSets.BLOCK).build());
            }
        });

        built.forEach((loc, table) -> cache.put(
                new ResourceLocation(loc.getNamespace(), "loot_tables/" + loc.getPath() + ".json"),
                LootTables.serialize(table).toString()
        ));
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return BCContent.loadedSets()
                .stream()
                .map(CrystalSet::blocks)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    protected void generate()
    {
        BCContent.loadedSets().forEach(set ->
        {
            add(set.getBuddingBlock(), noDrop());

            dropWhenSilkTouch(set.getSmallBud());
            dropWhenSilkTouch(set.getMediumBud());
            dropWhenSilkTouch(set.getLargeBud());

            add(set.getCluster(), block ->
                    createSilkTouchDispatchTable(
                            block,
                            OptionalLootItem.lootTableItem(set.getDroppedItem())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(set.getMaxDrops())))
                                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                                    .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                                    .otherwise(applyExplosionDecay(
                                            block,
                                            OptionalLootItem.lootTableItem(set.getDroppedItem())
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(set.getNormalDrops())))
                                    ))
                    )
            );
        });
    }
}
