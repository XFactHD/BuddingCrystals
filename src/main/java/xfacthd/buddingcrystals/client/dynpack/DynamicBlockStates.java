package xfacthd.buddingcrystals.client.dynpack;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingStateProvider;
import xfacthd.buddingcrystals.common.dynpack.DummyPackOutput;

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
        BCContent.loadedSets().forEach(set -> BuddingStateProvider.makeModels(this, set));
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
