package xfacthd.buddingcrystals.client.dynpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalSet;

import java.util.Map;
import java.util.TreeMap;

public class DynamicLanguage
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    @SuppressWarnings("deprecation")
    public static void run(Map<ResourceLocation, String> cache)
    {
        Map<String, String> translations = new TreeMap<>();

        BCContent.loadedSets().forEach(set ->
                translate(translations, set, set.getTranslation())
        );

        String data = GSON.toJson(translations);
        // Escape unicode after the fact so that it's not double escaped by GSON
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data);

        cache.put(new ResourceLocation(BuddingCrystals.MOD_ID, "lang/en_us.json"), data);
    }

    private static void translate(Map<String, String> translations, CrystalSet set, String name)
    {
        add(translations, set.getSmallBud(), "Small " + name + " Bud");
        add(translations, set.getMediumBud(), "Medium " + name + " Bud");
        add(translations, set.getLargeBud(), "Large " + name + " Bud");
        add(translations, set.getCluster(), name + " Cluster");
        add(translations, set.getBuddingBlock(), "Budding " + name);
    }

    private static void add(Map<String, String> translations, Block key, String value) { translations.put(key.getDescriptionId(), value); }
}
