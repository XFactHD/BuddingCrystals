package xfacthd.buddingcrystals.client.dynpack.generators;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerator;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.dynpack.DynPackUtils;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class BlockStateGenerator implements ResourceGenerator
{
    public static final MapCodec<BlockStateGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(gen -> gen.name)
    ).apply(inst, BlockStateGenerator::new));
    private static final ResourceLocation DUMMY = BuddingCrystals.rl("dummy");
    private static final PropertyDispatch FACING_DISPATCH = PropertyDispatch.property(BlockStateProperties.FACING)
            .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
            .select(Direction.UP, Variant.variant())
            .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
            .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
            .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
            .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));

    private final String name;
    private final Map<ResourceLocation, ExtModelTemplate> modelTemplates;
    private final Map<ResourceLocation, Supplier<MultiVariantGenerator>> blockstateTemplates;
    private final Set<ResourceLocation> allLocations = new HashSet<>();

    public BlockStateGenerator(CrystalSet set)
    {
        this(set.getName());
    }

    private BlockStateGenerator(String name)
    {
        this.name = name;
        this.modelTemplates = Map.of(
                BuddingCrystals.rl("models/block/" + CrystalSet.smallBudId(name) + ".json"),
                model("block/cross", "cutout", Map.of(TextureSlot.CROSS, BuddingCrystals.rl("block/small_bud/" + name))),
                BuddingCrystals.rl("models/block/" + CrystalSet.mediumBudId(name) + ".json"),
                model("block/cross", "cutout", Map.of(TextureSlot.CROSS, BuddingCrystals.rl("block/medium_bud/" + name))),
                BuddingCrystals.rl("models/block/" + CrystalSet.largeBudId(name) + ".json"),
                model("block/cross", "cutout", Map.of(TextureSlot.CROSS, BuddingCrystals.rl("block/large_bud/" + name))),
                BuddingCrystals.rl("models/block/" + CrystalSet.clusterId(name) + ".json"),
                model("block/cross", "cutout", Map.of(TextureSlot.CROSS, BuddingCrystals.rl("block/cluster/" + name))),
                BuddingCrystals.rl("models/block/" + CrystalSet.buddingBlockId(name) + ".json"),
                model("block/cube_all", "solid", Map.of(TextureSlot.ALL, BuddingCrystals.rl("block/budding/" + name))),

                BuddingCrystals.rl("models/item/" + CrystalSet.smallBudId(name) + ".json"),
                model("item/small_amethyst_bud", null, Map.of(TextureSlot.LAYER0, BuddingCrystals.rl("block/small_bud/" + name))),
                BuddingCrystals.rl("models/item/" + CrystalSet.mediumBudId(name) + ".json"),
                model("item/medium_amethyst_bud", null, Map.of(TextureSlot.LAYER0, BuddingCrystals.rl("block/medium_bud/" + name))),
                BuddingCrystals.rl("models/item/" + CrystalSet.largeBudId(name) + ".json"),
                model("item/large_amethyst_bud", null, Map.of(TextureSlot.LAYER0, BuddingCrystals.rl("block/large_bud/" + name))),
                BuddingCrystals.rl("models/item/" + CrystalSet.clusterId(name) + ".json"),
                model("item/amethyst_cluster", null, Map.of(TextureSlot.LAYER0, BuddingCrystals.rl("block/cluster/" + name))),
                BuddingCrystals.rl("models/item/" + CrystalSet.buddingBlockId(name) + ".json"),
                model(BuddingCrystals.rl("block/" + CrystalSet.buddingBlockId(name)), null, Map.of())
        );
        this.blockstateTemplates = Map.of(
                BuddingCrystals.rl("blockstates/" + CrystalSet.smallBudId(name) + ".json"),
                crossBlockState(CrystalSet.smallBudId(name)),
                BuddingCrystals.rl("blockstates/" + CrystalSet.mediumBudId(name) + ".json"),
                crossBlockState(CrystalSet.mediumBudId(name)),
                BuddingCrystals.rl("blockstates/" + CrystalSet.largeBudId(name) + ".json"),
                crossBlockState(CrystalSet.largeBudId(name)),
                BuddingCrystals.rl("blockstates/" + CrystalSet.clusterId(name) + ".json"),
                crossBlockState(CrystalSet.clusterId(name)),
                BuddingCrystals.rl("blockstates/" + CrystalSet.buddingBlockId(name) + ".json"),
                cubeBlockState(CrystalSet.buddingBlockId(name))
        );
        this.allLocations.addAll(modelTemplates.keySet());
        this.allLocations.addAll(blockstateTemplates.keySet());
    }

    private static ExtModelTemplate model(String parent, @Nullable String renderType, Map<TextureSlot, ResourceLocation> textures)
    {
        return model(ResourceLocation.withDefaultNamespace(parent), renderType, textures);
    }

    private static ExtModelTemplate model(ResourceLocation parent, @Nullable String renderType, Map<TextureSlot, ResourceLocation> textures)
    {
        return new ExtModelTemplate(parent, renderType, textures);
    }

    private static Supplier<MultiVariantGenerator> cubeBlockState(String blockId)
    {
        return Lazy.of(() ->
        {
            ResourceLocation blockLoc = BuddingCrystals.rl(blockId);
            return MultiVariantGenerator.multiVariant(
                    BuiltInRegistries.BLOCK.get(blockLoc),
                    Variant.variant().with(VariantProperties.MODEL, blockLoc.withPrefix("block/"))
            );
        });
    }

    private static Supplier<MultiVariantGenerator> crossBlockState(String blockId)
    {
        return Lazy.of(() ->
        {
            ResourceLocation blockLoc = BuddingCrystals.rl(blockId);
            return MultiVariantGenerator.multiVariant(
                    BuiltInRegistries.BLOCK.get(blockLoc),
                    Variant.variant().with(VariantProperties.MODEL, blockLoc.withPrefix("block/"))
            ).with(FACING_DISPATCH);
        });
    }

    @Override
    public Set<ResourceLocation> getLocations(ResourceGenerationContext context)
    {
        return allLocations;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> get(ResourceLocation outRl, ResourceGenerationContext context)
    {
        ExtModelTemplate modelTemplate = modelTemplates.get(outRl);
        if (modelTemplate != null)
        {
            String model = modelTemplate.generate();
            return model != null ? DynPackUtils.toIoSupplier(model) : null;
        }
        Supplier<MultiVariantGenerator> stateTemplate = blockstateTemplates.get(outRl);
        if (stateTemplate != null)
        {
            String blockState = stateTemplate.get().get().toString();
            return DynPackUtils.toIoSupplier(blockState);
        }
        return null;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public <T> DataResult<T> persistentCacheData(DynamicOps<T> ops, ResourceLocation location, ResourceGenerationContext context)
    {
        return DataResult.success(ops.empty());
    }

    @Override
    public MapCodec<? extends ResourceGenerator> codec()
    {
        return CODEC;
    }

    private static final class ExtModelTemplate extends ModelTemplate
    {
        private final Map<TextureSlot, ResourceLocation> textures;
        @Nullable
        private final ResourceLocation renderType;

        public ExtModelTemplate(ResourceLocation model, @Nullable String renderType, Map<TextureSlot, ResourceLocation> textures)
        {
            super(Optional.of(model), Optional.empty(), textures.keySet().toArray(TextureSlot[]::new));
            this.renderType = renderType != null ? ResourceLocation.withDefaultNamespace(renderType) : null;
            this.textures = textures;
        }

        @Nullable
        public String generate()
        {
            TextureMapping mapping = new TextureMapping();
            textures.forEach(mapping::put);
            String[] result = new String[1];
            create(DUMMY, mapping, (rl, elem) -> result[0] = elem.get().toString());
            return result[0];
        }

        @Override
        public JsonObject createBaseTemplate(ResourceLocation modelLocation, Map<TextureSlot, ResourceLocation> modelGetter)
        {
            JsonObject obj = super.createBaseTemplate(modelLocation, modelGetter);
            if (renderType != null)
            {
                obj.addProperty("render_type", renderType.toString());
            }
            return obj;
        }
    }
}
