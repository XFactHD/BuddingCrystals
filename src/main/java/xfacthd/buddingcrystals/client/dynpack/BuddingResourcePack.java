package xfacthd.buddingcrystals.client.dynpack;

import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import xfacthd.buddingcrystals.common.dynpack.BuddingPackResources;

import java.util.Map;

public final class BuddingResourcePack extends BuddingPackResources
{
    private final LanguageMetadataSection langMetadata = new LanguageMetadataSection(Map.of(
            "en_us", new LanguageInfo("US", "English", false)
    ));

    @SuppressWarnings("deprecation")
    public BuddingResourcePack()
    {
        super(PackType.CLIENT_RESOURCES, SharedConstants.RESOURCE_PACK_FORMAT);
    }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        //noinspection ConstantConditions
        new DynamicBlockStates(cache).run(null).join();
        DynamicLanguage.run(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer)
    {
        if (deserializer == LanguageMetadataSection.TYPE)
        {
            return (T) langMetadata;
        }
        return super.getMetadataSection(deserializer);
    }

    @Override
    public String packId()
    {
        return "BuddingCrystals JSON Crystal Data";
    }
}
