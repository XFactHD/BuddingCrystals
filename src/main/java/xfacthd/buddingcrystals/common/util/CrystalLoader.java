package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Stopwatch;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.block.BuddingCrystalBlock;
import xfacthd.buddingcrystals.common.data.BCCodecs;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("SameParameterValue")
public final class CrystalLoader
{
    private static final Pattern VALID_NAME = Pattern.compile("^[a-z][a-z\\d_]+$");
    public static final Path CRYSTAL_PATH = FMLPaths.GAMEDIR.get().resolve("buddingcrystals");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Codec<CrystalDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("compat_mod", "minecraft").forGetter(CrystalDefinition::compatMod),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("translation").forGetter(CrystalDefinition::translation),
            Codec.either(
                    Codec.pair(
                            ResourceLocation.CODEC.fieldOf("crystal_texture").codec(),
                            ResourceLocation.CODEC.fieldOf("budding_texture").codec()
                    ),
                    ResourceLocation.CODEC
            ).fieldOf("texture").forGetter(CrystalDefinition::eitherTexture),
            BCCodecs.intMin(0).fieldOf("growth_chance").forGetter(CrystalDefinition::growthChance),
            ResourceLocation.CODEC.fieldOf("dropped_item").forGetter(CrystalDefinition::dropName),
            ResourceLocation.CODEC.optionalFieldOf("recipe_item").forGetter(CrystalDefinition::recipeName),
            BCCodecs.floatMin(0).fieldOf("normal_drop_chance").forGetter(CrystalDefinition::normalDrop),
            BCCodecs.floatMin(0).fieldOf("max_drop_chance").forGetter(CrystalDefinition::maxDrop)
    ).apply(instance, CrystalDefinition::new));

    public static void loadUserSets()
    {
        LOGGER.info("Loading custom crystal definitions");
        Stopwatch stopwatch = Stopwatch.createStarted();

        FileUtils.getOrCreateDirectory(CRYSTAL_PATH, "BuddingCrystals crystal definitions");

        try (Stream<Path> paths = Files.list(CRYSTAL_PATH))
        {
            paths.filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().endsWith(".json"))
                    .filter(CrystalLoader::filterBuiltins)
                    .forEach(filePath ->
                    {
                        String name = getName(filePath);
                        if (!VALID_NAME.matcher(name).matches())
                        {
                            LOGGER.error("Invalid crystal definition name {}, does not match pattern {} ", name, VALID_NAME.pattern());
                            return;
                        }

                        try
                        {
                            JsonObject json = readJsonFile(filePath);
                            loadDefinition(name, json);
                        }
                        catch (JsonParseException e)
                        {
                            LOGGER.error("Encountered an error while loading crystal definition for '" + name + "'", e);
                        }
                    });
        }
        catch (IOException e)
        {
            LOGGER.error("Encountered an error while loading crystal definitions", e);
        }

        stopwatch.stop();
        LOGGER.info("Crystal definitions loaded in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public static void overrideFromJson(String name, CrystalSet.Builder builder)
    {
        Path file = CRYSTAL_PATH.resolve(name + ".json");
        if (Files.exists(file))
        {
            CrystalDefinition def;

            try
            {
                JsonObject json = readJsonFile(file);
                def = parseJson(json);
            }
            catch (JsonParseException e)
            {
                LOGGER.error("Encountered an error while loading override for builtin CrystalSet '" + name + "'", e);
                return;
            }

            builder.translation(def.translation)
                    .compatMod(def.compatMod)
                    .buddingSourceTexture(def.buddingTexture)
                    .crystalSourceTexture(def.crystalTexture)
                    .growthChance(def.growthChance)
                    .ingredient(def.ingredientName)
                    .drop(def.dropName)
                    .normalDrop(def.normalDrop)
                    .maxDrop(def.maxDrop);
        }
    }

    public static void updateFromJson(Update type)
    {
        BCContent.allSets().forEach(set ->
        {
            Path filePath = CRYSTAL_PATH.resolve(set.getName() + ".json");
            if (!Files.exists(filePath))
            {
                if (!BCContent.builtinSets().contains(set))
                {
                    LOGGER.error("Crystal definition for '{}' went missing after startup!", set.getName());
                }
                return;
            }

            try
            {
                JsonObject json = readJsonFile(filePath);
                CrystalDefinition def = parseJson(json);
                if (type == Update.SERVER)
                {
                    set.updateServerData(def.dropName, def.ingredientName, def.normalDrop, def.maxDrop);
                }
                else
                {
                    set.updateClientData(def.translation, def.crystalTexture, def.buddingTexture);
                }
            }
            catch (JsonParseException e)
            {
                LOGGER.error("Encountered an error while updating crystal definition for '" + set.getName() + "'", e);
            }
        });
    }

    public static String export(String name)
    {
        CrystalSet set = BCContent.BUILTIN_SETS.get(name);
        CrystalDefinition def = CrystalDefinition.fromSet(set);
        JsonElement json = CODEC.encodeStart(JsonOps.INSTANCE, def).result().orElseThrow();
        return GSON.toJson(json);
    }



    private static JsonObject readJsonFile(Path path)
    {
        try (Reader reader = Files.newBufferedReader(path))
        {
            return GsonHelper.fromJson(GSON, reader, JsonObject.class);
        }
        catch (IOException e)
        {
            throw new JsonSyntaxException(e);
        }
    }

    private static void loadDefinition(String name, JsonObject json)
    {
        CrystalDefinition def = parseJson(json);

        RegistryObject<Item> drop = RegistryObject.create(def.dropName, ForgeRegistries.ITEMS);
        RegistryObject<Item> ingredient = drop;
        if (!def.dropName.equals(def.ingredientName))
        {
            ingredient = RegistryObject.create(def.ingredientName, ForgeRegistries.ITEMS);
        }

        RegistryObject<Block> smallBud = CrystalSet.Builder.register("small_" + name + "_bud", CrystalSet.Builder::smallBud, def.compatMod);
        RegistryObject<Block> mediumBud = CrystalSet.Builder.register("medium_" + name + "_bud", CrystalSet.Builder::mediumBud, def.compatMod);
        RegistryObject<Block> largeBud = CrystalSet.Builder.register("large_" + name + "_bud", CrystalSet.Builder::largeBud, def.compatMod);
        RegistryObject<Block> cluster = CrystalSet.Builder.register(name + "_cluster", CrystalSet.Builder::cluster, def.compatMod);
        BudSet budSet = new BudSet(smallBud, mediumBud, largeBud, cluster);

        RegistryObject<Block> buddingBlock = CrystalSet.Builder.register(
                "budding_" + name,
                () -> new BuddingCrystalBlock(
                        budSet,
                        def.growthChance,
                        BlockBehaviour.Properties.of(Material.AMETHYST)
                                .randomTicks()
                                .strength(1.5F)
                                .sound(SoundType.AMETHYST)
                                .requiresCorrectToolForDrops()
                ),
                def.compatMod
        );

        CrystalSet set = new CrystalSet(
                def.compatMod,
                name,
                def.translation,
                def.crystalTexture,
                def.buddingTexture,
                def.growthChance,
                buddingBlock,
                budSet,
                drop,
                ingredient,
                def.normalDrop,
                def.maxDrop
        );

        BCContent.ALL_SETS.put(name, set);
        BCContent.LOADED_SETS.put(name, set);
    }

    private static CrystalDefinition parseJson(JsonObject json)
    {
        DataResult<CrystalDefinition> result = CODEC.parse(JsonOps.INSTANCE, json);
        if (result.error().isPresent())
        {
            throw new JsonParseException(result.error().get().message());
        }
        return result.result().orElseThrow();
    }

    private static String getName(Path path)
    {
        String file = path.getFileName().toString();
        return file.substring(0, file.indexOf('.'));
    }

    private static boolean filterBuiltins(Path path)
    {
        String name = getName(path);
        return !name.equals("amethyst") && !BCContent.BUILTIN_SETS.containsKey(name);
    }



    public enum Update
    {
        SERVER,
        CLIENT;

        public static Update fromPackType(PackType type)
        {
            return switch (type)
            {
                case CLIENT_RESOURCES -> CLIENT;
                case SERVER_DATA -> SERVER;
            };
        }
    }

    private record CrystalDefinition(
            String compatMod,
            String translation,
            ResourceLocation crystalTexture,
            ResourceLocation buddingTexture,
            int growthChance,
            ResourceLocation ingredientName,
            ResourceLocation dropName,
            float normalDrop,
            float maxDrop
    )
    {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public CrystalDefinition(
                String compatMod,
                String translation,
                Either<Pair<ResourceLocation, ResourceLocation>, ResourceLocation> texture,
                int growthChance,
                ResourceLocation dropName,
                Optional<ResourceLocation> ingredientName,
                float normalDrop,
                float maxDrop
        ) {
            this(compatMod, translation, either(texture, Pair::getFirst), either(texture, Pair::getSecond), growthChance, dropName, ingredientName.orElse(dropName), normalDrop, maxDrop);
        }

        public Either<Pair<ResourceLocation, ResourceLocation>, ResourceLocation> eitherTexture()
        {
            if (crystalTexture.equals(buddingTexture))
            {
                return Either.right(crystalTexture);
            }
            return Either.left(Pair.of(crystalTexture, buddingTexture));
        }

        public Optional<ResourceLocation> recipeName() { return Optional.of(ingredientName); }

        private static ResourceLocation either(
                Either<Pair<ResourceLocation, ResourceLocation>, ResourceLocation> texture,
                Function<Pair<ResourceLocation, ResourceLocation>, ResourceLocation> pairMapper
        ) {
            return texture.mapLeft(pairMapper).left().orElse(texture.right().orElseThrow());
        }

        public static CrystalDefinition fromSet(CrystalSet set)
        {
            return new CrystalDefinition(
                    set.getCompatMod(),
                    set.getTranslation(),
                    set.getCrystalSourceTexture(),
                    set.getBuddingSourceTexture(),
                    set.getGrowthChance(),
                    ForgeRegistries.ITEMS.getKey(set.getIngredient()),
                    ForgeRegistries.ITEMS.getKey(set.getDroppedItem()),
                    set.getNormalDrops(),
                    set.getMaxDrops()
            );
        }
    }
}
