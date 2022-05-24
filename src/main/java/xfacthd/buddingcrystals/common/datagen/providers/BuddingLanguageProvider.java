package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalSet;

public final class BuddingLanguageProvider extends LanguageProvider
{
    public BuddingLanguageProvider(DataGenerator gen) { super(gen, BuddingCrystals.MOD_ID, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(BuddingCrystals.CREATIVE_TAB.getDisplayName().getString(), "BuddingCrystals");

        translate(BCContent.REDSTONE, "Redstone");
        translate(BCContent.DIAMOND, "Diamond");
        translate(BCContent.EMERALD, "Emerald");
        translate(BCContent.LAPIS_LAZULI, "Lapis Lazuli");
        translate(BCContent.GLOWSTONE, "Glowstone");
        translate(BCContent.NETHER_QUARTZ, "Nether Quartz");
        translate(BCContent.CERTUS_QUARTZ, "Certus Quartz");
        translate(BCContent.FLUIX, "Fluix");
        translate(BCContent.SALT, "Salt");
        translate(BCContent.FLUORITE, "Fluorite");
    }

    private void translate(CrystalSet set, String name)
    {
        add(set.getSmallBud(), "Small " + name + " Bud");
        add(set.getMediumBud(), "Medium " + name + " Bud");
        add(set.getLargeBud(), "Large " + name + " Bud");
        add(set.getCluster(), name + " Cluster");
        add(set.getBuddingBlock(), "Budding " + name);
    }
}
