package xfacthd.buddingcrystals.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.List;
import java.util.function.Consumer;

public final class OptionalLootItem extends LootPoolSingletonContainer
{
    @SuppressWarnings("deprecation")
    public static final Codec<OptionalLootItem> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BuiltInRegistries.ITEM.holderByNameCodec()
                    .fieldOf("name")
                    .orElse(Items.AIR.builtInRegistryHolder())
                    .forGetter(lootItem -> lootItem.item)
    ).and(singletonFields(inst)).apply(inst, OptionalLootItem::new));

    private final Holder<Item> item;

    private OptionalLootItem(Holder<Item> item, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions)
    {
        super(weight, quality, conditions, functions);
        this.item = item;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> stackConsumer, LootContext lootContext)
    {
        stackConsumer.accept(new ItemStack(this.item));
    }

    @SuppressWarnings("deprecation")
    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike item)
    {
        return simpleBuilder((weight, quality, conditions, functions) ->
                new OptionalLootItem(item.asItem().builtInRegistryHolder(), weight, quality, conditions, functions)
        );
    }

    @Override
    public LootPoolEntryType getType()
    {
        return BCContent.OPTIONAL_LOOT_ITEM.value();
    }
}
