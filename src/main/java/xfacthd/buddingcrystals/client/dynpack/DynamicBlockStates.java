package xfacthd.buddingcrystals.client.dynpack;

import com.google.common.base.Preconditions;
import net.minecraft.DetectedVersion;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.BuddingStateProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

public final class DynamicBlockStates extends BlockStateProvider
{
    private final Map<ResourceLocation, String> cache;

    public DynamicBlockStates(Map<ResourceLocation, String> cache)
    {
        super(new DataGenerator(Path.of(""), List.of(), DetectedVersion.tryDetectVersion(), true),
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
    public void run(CachedOutput cache)
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
            ResourceLocation blockName = Preconditions.checkNotNull(ForgeRegistries.BLOCKS.getKey(entry.getKey()));
            ResourceLocation stateLocation = new ResourceLocation(blockName.getNamespace(), "blockstates/" + blockName.getPath() + ".json");
            this.cache.put(stateLocation, entry.getValue().toJson().toString());
        }
    }
}
