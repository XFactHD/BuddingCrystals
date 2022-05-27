package xfacthd.buddingcrystals.common.datagen.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DummiedExistingFileHelper extends ExistingFileHelper
{
    private final ExistingFileHelper fileHelper;
    private final List<ResourceLocation> dummies;

    public DummiedExistingFileHelper(ExistingFileHelper fileHelper, List<ResourceLocation> dummies)
    {
        super(Collections.emptyList(), Collections.emptySet(), false, null, null);
        this.fileHelper = fileHelper;
        this.dummies = dummies;
    }

    @Override
    public void trackGenerated(ResourceLocation loc, IResourceType type)
    {
        fileHelper.trackGenerated(loc, type);
    }

    @Override
    public void trackGenerated(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix)
    {
        fileHelper.trackGenerated(loc, packType, pathSuffix, pathPrefix);
    }

    @Override
    public Resource getResource(ResourceLocation loc, PackType packType) throws IOException
    {
        return fileHelper.getResource(loc, packType);
    }

    @Override
    public Resource getResource(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) throws IOException
    {
        return fileHelper.getResource(loc, packType, pathSuffix, pathPrefix);
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType packType)
    {
        if (packType == PackType.CLIENT_RESOURCES && hasDummy(loc))
        {
            return true;
        }
        return fileHelper.exists(loc, packType);
    }

    @Override
    public boolean exists(ResourceLocation loc, IResourceType type)
    {
        if (type.getPackType() == PackType.CLIENT_RESOURCES && hasDummy(getLocation(loc, type.getSuffix(), type.getPrefix())))
        {
            return true;
        }
        return fileHelper.exists(loc, type);
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix)
    {
        if (packType == PackType.CLIENT_RESOURCES && hasDummy(getLocation(loc, pathSuffix, pathPrefix)))
        {
            return true;
        }
        return fileHelper.exists(loc, packType, pathSuffix, pathPrefix);
    }

    private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix)
    {
        return new ResourceLocation(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
    }

    private boolean hasDummy(ResourceLocation loc) { return dummies.contains(loc); }

    @Override
    public boolean isEnabled() { return fileHelper.isEnabled(); }
}
