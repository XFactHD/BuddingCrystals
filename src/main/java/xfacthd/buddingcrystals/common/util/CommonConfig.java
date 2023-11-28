package xfacthd.buddingcrystals.common.util;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class CommonConfig
{
    public static final ModConfigSpec SPEC;
    public static final CommonConfig INSTANCE;

    private static final Object2BooleanMap<String> crystalEnabled = new Object2BooleanArrayMap<>();

    private final Map<String, ModConfigSpec.BooleanValue> crystalEnabledValues = new HashMap<>();

    static
    {
        final Pair<CommonConfig, ModConfigSpec> configSpecPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public CommonConfig(ModConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("crystals");
        Streams.concat(Stream.of(BCContent.AMETHYST), BCContent.builtinSets().stream()).forEach(set ->
        {
            ModConfigSpec.BooleanValue config = builder
                    .comment("Allow crafting of budding " + set.getTranslation() + " block")
                    .translation(set.getConfigTranslation())
                    .define(set.getConfigString(), true);
            crystalEnabledValues.put(set.getConfigString(), config);
        });
        builder.pop();
    }

    public static boolean isEnabled(String config)
    {
        return crystalEnabled.getBoolean(config);
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getModId().equals(BuddingCrystals.MOD_ID))
        {
            for (String config : crystalEnabledValues.keySet())
            {
                crystalEnabled.put(config, crystalEnabledValues.get(config).get().booleanValue());
            }
        }
    }
}
