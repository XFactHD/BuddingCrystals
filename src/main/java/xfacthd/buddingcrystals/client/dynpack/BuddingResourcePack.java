package xfacthd.buddingcrystals.client.dynpack;

import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.dynpack.BuddingPackResources;

import java.util.Map;
import java.util.Set;

public final class BuddingResourcePack extends BuddingPackResources
{
    private final LanguageMetadataSection langMetadata = new LanguageMetadataSection(Set.of(
            new LanguageInfo("en_us", "US", "English", false)
    ));

    public BuddingResourcePack() { super(PackType.CLIENT_RESOURCES, 8); }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        //noinspection ConstantConditions
        new DynamicBlockStates(cache).run(null);
        DynamicLanguage.run(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer)
    {
        if (deserializer == LanguageMetadataSection.SERIALIZER)
        {
            return (T) langMetadata;
        }
        return super.getMetadataSection(deserializer);
    }

    @Override
    public String getName() { return "BuddingCrystals JSON Crystal Data"; }
}
