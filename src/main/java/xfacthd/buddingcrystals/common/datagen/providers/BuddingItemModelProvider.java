package xfacthd.buddingcrystals.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.buddingcrystals.BuddingCrystals;

public final class BuddingItemModelProvider extends ItemModelProvider
{
    public BuddingItemModelProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, BuddingCrystals.MOD_ID, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        withExistingParent("crystal_catalyst", "item/generated").texture("layer0", modLoc("item/crystal_catalyst"));
    }
}
