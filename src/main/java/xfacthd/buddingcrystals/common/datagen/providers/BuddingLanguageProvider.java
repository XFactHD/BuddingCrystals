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

        add(BCContent.AMETHYST.getConfigTranslation(), "Allow crafting of budding Amethyst block");
        BCContent.builtinSets().forEach(set ->
        {
            translate(set, set.getTranslation());
            add(set.getConfigTranslation(), "Allow crafting of budding " + set.getTranslation() + " block");
        });

        add(BCContent.CRYSTAL_CATALYST.get(), "Crystal Catalyst");
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
