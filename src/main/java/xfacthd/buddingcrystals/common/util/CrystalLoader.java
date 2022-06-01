package xfacthd.buddingcrystals.common.util;

import com.google.common.base.Stopwatch;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("SameParameterValue")
public class CrystalLoader
{
    private static final Pattern VALID_NAME = Pattern.compile("^[a-z][a-z\\d_]+$");
    private static final Path CRYSTAL_PATH = FMLPaths.GAMEDIR.get().resolve("buddingcrystals");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

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

    private static JsonObject readJsonFile(Path path)
    {
        try
        {
            Reader reader = Files.newBufferedReader(path);
            JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
            reader.close();
            return json;
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
        Predicate<String> stringValidator = s -> s != null && !s.isEmpty();

        String compatMod = getString(json, "compat_mod", "minecraft", stringValidator, "Compat mod must not be empty");
        String translation = getString(json, "translation", stringValidator, "Translation must not be empty");

        String crystalTex;
        String buddingTex;
        if (json.has("crystal_texture") && json.has("budding_texture"))
        {
            crystalTex = getString(json, "crystal_texture", stringValidator, "Crystal texture must not be empty");
            buddingTex = getString(json, "budding_texture", stringValidator, "Budding texture must not be empty");
        }
        else
        {
            crystalTex = buddingTex = getString(json, "texture", stringValidator, "Texture must not be empty");
        }
        ResourceLocation crystalTexture = ResourceLocation.tryParse(crystalTex);
        ResourceLocation buddingTexture = ResourceLocation.tryParse(buddingTex);

        int growthChance = getInt(json, "growth_chance", i -> i > 0, "Growth chance must be higher than 0");
        ResourceLocation dropName = getResLoc(json, "dropped_item");
        ResourceLocation recipeName = getResLoc(json, "recipe_item", dropName.toString());
        float normalDrop = getFloat(json, "normal_drop_chance", f -> f > 0F, "Normal drop count must be higher than 0");
        float maxDrop = getFloat(json, "max_drop_chance", f -> f > 0F, "Max drop count must be higher than 0");

        return new CrystalDefinition(compatMod, translation, crystalTexture, buddingTexture, growthChance, recipeName, dropName, normalDrop, maxDrop);
    }

    private static ResourceLocation getResLoc(JsonObject json, String key, String _default)
    {
        try
        {
            return new ResourceLocation(GsonHelper.getAsString(json, key, _default));
        }
        catch (ResourceLocationException e)
        {
            throw new JsonSyntaxException(e);
        }
    }

    private static ResourceLocation getResLoc(JsonObject json, String key)
    {
        try
        {
            return new ResourceLocation(GsonHelper.getAsString(json, key));
        }
        catch (ResourceLocationException e)
        {
            throw new JsonSyntaxException(e);
        }
    }

    private static String getString(JsonObject json, String key, String _default, Predicate<String> validator, String message)
    {
        String value = GsonHelper.getAsString(json, key, _default);
        return validate(value, validator, message);
    }

    private static String getString(JsonObject json, String key, Predicate<String> validator, String message)
    {
        String value = GsonHelper.getAsString(json, key);
        return validate(value, validator, message);
    }

    private static int getInt(JsonObject json, String key, Predicate<Integer> validator, String message)
    {
        int value = GsonHelper.getAsInt(json, key);
        return validate(value, validator, message);
    }

    private static float getFloat(JsonObject json, String key, Predicate<Float> validator, String message)
    {
        float value = GsonHelper.getAsInt(json, key);
        return validate(value, validator, message);
    }

    private static <T> T validate(T value, Predicate<T> validator, String message)
    {
        if (!validator.test(value))
        {
            throw new JsonSyntaxException(message);
        }
        return value;
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
    ) { }
}
