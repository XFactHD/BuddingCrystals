package xfacthd.buddingcrystals.common.dynpack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.util.CrystalLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class BuddingPackResources implements PackResources
{
    protected static final Logger LOGGER = LogUtils.getLogger();
    private final PackMetadataSection packMetadata;
    private final Map<ResourceLocation, String> dataCache = new HashMap<>();
    private final PackType type;
    private final Set<String> namespaces;

    protected BuddingPackResources(PackType type, int packFormat, Set<String> namespaces)
    {
        this.type = type;
        this.namespaces = namespaces;
        this.packMetadata = new PackMetadataSection(new TextComponent(getName()), packFormat);

        String typeName = type == PackType.CLIENT_RESOURCES ? "resourcepack" : "datapack";

        LOGGER.info("Reloading crystal definitions for dynamic {} reload", typeName);

        Stopwatch stopwatch = Stopwatch.createStarted();
        CrystalLoader.updateFromJson(CrystalLoader.Update.fromPackType(type));
        stopwatch.stop();

        LOGGER.info("Reloaded crystal definitions in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        LOGGER.info("Generating dynamic {} for BuddingCrystals", typeName);

        stopwatch = Stopwatch.createStarted();
        buildResources(dataCache);
        stopwatch.stop();

        LOGGER.info("Generated dynamic {} in {}ms", typeName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public InputStream getRootResource(String fileName) { return null; }

    @Override
    public final InputStream getResource(PackType type, ResourceLocation location) throws IOException
    {
        if (hasResource(type, location))
        {
            return new ByteArrayInputStream(dataCache.get(location).getBytes(StandardCharsets.UTF_8));
        }
        throw new IOException(String.format("Couldn't find resource %s in BuddingCrystals dynamic data pack", location));
    }

    @Override
    public final Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
    {
        if (type == this.type)
        {
            int pathLen = path.length();
            return dataCache.keySet()
                    .stream()
                    .filter(loc -> loc.getNamespace().equals(namespace))
                    .filter(loc -> loc.getPath().startsWith(path))
                    .filter(loc -> filter.test(loc.toString()))
                    .filter(loc -> StringUtils.countMatches(loc.getPath().substring(pathLen), '/') <= maxDepth)
                    .toList();
        }
        return Set.of();
    }

    @Override
    public final boolean hasResource(PackType type, ResourceLocation location)
    {
        if (type == this.type)
        {
            return dataCache.containsKey(location);
        }
        return false;
    }

    @Override
    public final Set<String> getNamespaces(PackType type) { return type == this.type ? namespaces : Set.of(); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer)
    {
        if (deserializer == PackMetadataSection.SERIALIZER)
        {
            return (T) packMetadata;
        }
        return null;
    }

    @Override
    public void close() { }

    @Override
    public final boolean isHidden() { return true; }

    protected abstract void buildResources(Map<ResourceLocation, String> cache);

    protected static ResourceLocation mcRl(String path) { return new ResourceLocation("minecraft", path); }

    protected static ResourceLocation bcRl(String path) { return new ResourceLocation(BuddingCrystals.MOD_ID, path); }
}
