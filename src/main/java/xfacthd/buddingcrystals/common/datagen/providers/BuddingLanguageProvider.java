package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.neoforge.common.data.LanguageProvider;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.client.util.ExportCommand;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.network.task.CrystalSetsConfigTask;
import xfacthd.buddingcrystals.common.util.CrystalSet;
import xfacthd.buddingcrystals.common.util.CrystalTab;

public final class BuddingLanguageProvider extends LanguageProvider
{
    public BuddingLanguageProvider(PackOutput output)
    {
        super(output, BuddingCrystals.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        add(CrystalTab.TAB_TITLE.getString(), "BuddingCrystals");

        add(BCContent.AMETHYST.getConfigTranslation(), "Allow crafting of budding Amethyst block");
        BCContent.builtinSets().forEach(set ->
        {
            translate(set, set.getTranslation());
            add(set.getConfigTranslation(), "Allow crafting of budding " + set.getTranslation() + " block");
        });

        add(BCContent.CRYSTAL_CATALYST.value(), "Crystal Catalyst");

        add(ExportCommand.MSG_NO_SUCH_CRYSTAL     , "No crystal named '%s' exists");
        add(ExportCommand.MSG_EXPORT_ERROR        , "Encountered an error while exporting crystal definition named '%s': %s");
        add(ExportCommand.MSG_CRYSTALS_EXPORTED   , "Exported %d out of %d builtin crystal definitions, skipped %d existing files");
        add(ExportCommand.MSG_CRYSTALS_OVERWRITTEN, "Exported %d out of %d builtin crystal definitions, %d existing files were overwritten");
        add(ExportCommand.MSG_CRYSTAL_EXPORTED    , "Exported crystal definition named '%s'");
        add(ExportCommand.MSG_CRYSTAL_OVERWRITTEN , "Exported crystal definition named '%s', existing file was overwritten");
        add(ExportCommand.MSG_CRYSTAL_EXISTS      , "File for crystal definition named '%s' already exists");

        add(CrystalSetsConfigTask.MSG_ADDITIONAL_SETS_SERVER, "Server has additional crystal sets: %s");
        add(CrystalSetsConfigTask.MSG_ADDITIONAL_SETS_CLIENT, "Client has additional crystal sets: %s");
        add(CrystalSetsConfigTask.MSG_CHECK_FILES_MATCH, "Make sure your server and client have the same files in the crystal configuration directory.");
    }

    private void translate(CrystalSet set, String name)
    {
        add(set.getSmallBud(), "Small " + name + " Bud");
        add(set.getMediumBud(), "Medium " + name + " Bud");
        add(set.getLargeBud(), "Large " + name + " Bud");
        add(set.getCluster(), name + " Cluster");
        add(set.getBuddingBlock(), "Budding " + name);
    }

    private void add(Component key, String value)
    {
        ComponentContents contents = key.getContents();
        if (!(contents instanceof TranslatableContents translatable))
        {
            throw new IllegalArgumentException("Component must be translatable");
        }
        add(translatable.getKey(), value);
    }
}
