package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.data.OptionalLootItem;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.List;
import java.util.Set;

public final class BuddingLootTableProvider extends LootTableProvider
{
    public BuddingLootTableProvider(PackOutput output)
    {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)
        ));
    }

    private static class BlockLootTable extends BlockLootSubProvider
    {
        public BlockLootTable()
        {
            super(Set.of(), FeatureFlags.VANILLA_SET);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return BCContent.builtinSets().stream()
                    .map(CrystalSet::blocks)
                    .flatMap(List::stream)
                    .toList();
        }

        @Override
        protected void generate()
        {
            BCContent.builtinSets().forEach(set ->
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
}
