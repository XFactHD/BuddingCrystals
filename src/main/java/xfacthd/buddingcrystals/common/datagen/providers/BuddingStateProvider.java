package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingStateProvider extends BlockStateProvider
{
    public BuddingStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, BuddingCrystals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        BCContent.builtinSets().forEach(set -> makeModels(this, set));
    }

    public static void makeModels(BlockStateProvider provider, CrystalSet set)
    {
        cluster(provider, set.getSmallBud(), "item/small_amethyst_bud", rl("block/small_bud/" + set.getName()));
        cluster(provider, set.getMediumBud(), "item/medium_amethyst_bud", rl("block/medium_bud/" + set.getName()));
        cluster(provider, set.getLargeBud(), "item/large_amethyst_bud", rl("block/large_bud/" + set.getName()));
        cluster(provider, set.getCluster(), "item/amethyst_cluster", rl("block/cluster/" + set.getName()));
        buddingBlock(provider, set.getBuddingBlock(), set.getName());
    }

    private static void cluster(BlockStateProvider provider, Block block, String itemParent, ResourceLocation texture)
    {
        //noinspection ConstantConditions
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = provider.models()
                .withExistingParent(name, provider.modLoc("block/cross"))
                .texture("cross", texture)
                .renderType("cutout");

        provider.getVariantBuilder(block).forAllStatesExcept(state ->
        {
            int rotX;
            int rotY;

            switch (state.getValue(AmethystClusterBlock.FACING))
            {
                case UP ->
                {
                    rotX = 0;
                    rotY = 0;
                }
                case DOWN ->
                {
                    rotX = 180;
                    rotY = 0;
                }
                case NORTH ->
                {
                    rotX = 90;
                    rotY = 0;
                }
                case EAST ->
                {
                    rotX = 90;
                    rotY = 90;
                }
                case SOUTH ->
                {
                    rotX = 90;
                    rotY = 180;
                }
                case WEST ->
                {
                    rotX = 90;
                    rotY = 270;
                }
                default -> throw new IllegalArgumentException("Invalid direction");
            }

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        }, AmethystClusterBlock.WATERLOGGED);

        provider.itemModels().withExistingParent(name, itemParent).texture("layer0", texture);
    }

    private static void buddingBlock(BlockStateProvider provider, Block block, String name)
    {
        //noinspection ConstantConditions
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        provider.simpleBlock(block, provider.models()
                .withExistingParent(path, provider.modLoc("block/cube_all"))
                .texture("all", rl("block/budding/" + name))
                .renderType("solid")
        );

        provider.itemModels().withExistingParent(path, provider.modLoc("block/" + path));
    }

    private static ResourceLocation rl(String path)
    {
        return new ResourceLocation(BuddingCrystals.MOD_ID, path);
    }
}
