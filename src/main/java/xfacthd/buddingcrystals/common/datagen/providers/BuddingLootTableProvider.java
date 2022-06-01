package xfacthd.buddingcrystals.common.datagen.providers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.data.OptionalLootItem;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.*;
import java.util.function.*;

public final class BuddingLootTableProvider extends LootTableProvider
{
    public BuddingLootTableProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return Collections.singletonList(Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK));
    }

    private static class BlockLootTable extends BlockLoot
    {
        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return BCContent.builtinSets().stream()
                    .map(CrystalSet::blocks)
                    .flatMap(List::stream)
                    .toList();
        }

        @Override
        protected void addTables()
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
