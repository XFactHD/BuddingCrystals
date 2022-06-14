package xfacthd.buddingcrystals.common.util;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class CommonConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final CommonConfig INSTANCE;

    private static final Object2BooleanMap<String> crystalEnabled = new Object2BooleanArrayMap<>();

    private final Map<String, ForgeConfigSpec.BooleanValue> crystalEnabledValues = new HashMap<>();

    static
    {
        final Pair<CommonConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public CommonConfig(ForgeConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("crystals");
        Streams.concat(Stream.of(BCContent.AMETHYST), BCContent.builtinSets().stream()).forEach(set ->
        {
            ForgeConfigSpec.BooleanValue config = builder
                    .comment("Allow crafting of budding " + set.getTranslation() + " block")
                    .translation(set.getConfigTranslation())
                    .define(set.getConfigString(), true);
            crystalEnabledValues.put(set.getConfigString(), config);
        });
        builder.pop();
    }

    public static boolean isEnabled(String config) { return crystalEnabled.getBoolean(config); }

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
