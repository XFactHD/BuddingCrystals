package xfacthd.buddingcrystals.client.dynpack;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.dynpack.DummyPackOutput;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class DynamicBlockStates extends BlockStateProvider
{
    private final Map<ResourceLocation, String> cache;

    public DynamicBlockStates(Map<ResourceLocation, String> cache)
    {
        super(DummyPackOutput.INSTANCE,
                BuddingCrystals.MOD_ID,
                new ExistingFileHelper(List.of(), Set.of(), false, null, null)
        );
        this.cache = cache;
    }

    @Override
    protected void registerStatesAndModels()
    {
        BCContent.allActiveSets().forEach(this::makeModels);
    }

    public void makeModels(CrystalSet set)
    {
        cluster(set.getSmallBud(), "item/small_amethyst_bud", rl("block/small_bud/" + set.getName()));
        cluster(set.getMediumBud(), "item/medium_amethyst_bud", rl("block/medium_bud/" + set.getName()));
        cluster(set.getLargeBud(), "item/large_amethyst_bud", rl("block/large_bud/" + set.getName()));
        cluster(set.getCluster(), "item/amethyst_cluster", rl("block/cluster/" + set.getName()));
        buddingBlock(set.getBuddingBlock(), set.getName());
    }

    private void cluster(Block block, String itemParent, ResourceLocation texture)
    {
        //noinspection ConstantConditions
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(name, modLoc("block/cross"))
                .texture("cross", texture)
                .renderType("cutout");

        getVariantBuilder(block).forAllStatesExcept(state ->
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

        itemModels().withExistingParent(name, itemParent).texture("layer0", texture);
    }

    private void buddingBlock(Block block, String name)
    {
        //noinspection ConstantConditions
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        simpleBlock(block, models()
                .withExistingParent(path, modLoc("block/cube_all"))
                .texture("all", rl("block/budding/" + name))
                .renderType("solid")
        );

        itemModels().withExistingParent(path, modLoc("block/" + path));
    }

    private static ResourceLocation rl(String path)
    {
        return new ResourceLocation(BuddingCrystals.MOD_ID, path);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        try
        {
            Method clear = ObfuscationReflectionHelper.findMethod(ModelProvider.class, "clear");
            clear.invoke(models());
            clear.invoke(itemModels());
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        registeredBlocks.clear();

        registerStatesAndModels();

        models().generatedModels.values().forEach(builder ->
        {
            ResourceLocation modelName = builder.getLocation();
            ResourceLocation modelLocation = new ResourceLocation(modelName.getNamespace(), "models/" + modelName.getPath() + ".json");
            this.cache.put(modelLocation, builder.toJson().toString());
        });
        itemModels().generatedModels.values().forEach(builder ->
        {
            ResourceLocation modelName = builder.getLocation();
            ResourceLocation modelLocation = new ResourceLocation(modelName.getNamespace(), "models/" + modelName.getPath() + ".json");
            this.cache.put(modelLocation, builder.toJson().toString());
        });

        for (Map.Entry<Block, IGeneratedBlockState> entry : registeredBlocks.entrySet())
        {
            ResourceLocation blockName = Preconditions.checkNotNull(BuiltInRegistries.BLOCK.getKey(entry.getKey()));
            ResourceLocation stateLocation = new ResourceLocation(blockName.getNamespace(), "blockstates/" + blockName.getPath() + ".json");
            this.cache.put(stateLocation, entry.getValue().toJson().toString());
        }

        return CompletableFuture.completedFuture(null);
    }
}
