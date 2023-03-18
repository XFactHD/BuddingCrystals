package xfacthd.buddingcrystals.common.dynpack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.util.CrystalLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BuddingPackResources implements PackResources
{
    protected static final Logger LOGGER = LogUtils.getLogger();
    private final PackMetadataSection packMetadata;
    private final Map<ResourceLocation, String> dataCache = new ConcurrentHashMap<>();
    private final PackType type;
    private final Set<String> namespaces;

    protected BuddingPackResources(PackType type, int packFormat)
    {
        this.type = type;
        this.packMetadata = new PackMetadataSection(Component.literal(packId()), packFormat);

        String typeName = type == PackType.CLIENT_RESOURCES ? "resourcepack" : "datapack";

        LOGGER.info("Reloading crystal definitions for dynamic {} reload", typeName);

        Stopwatch stopwatch = Stopwatch.createStarted();
        CrystalLoader.updateFromJson(CrystalLoader.Update.fromPackType(type));
        stopwatch.stop();

        LOGGER.info("Reloaded crystal definitions in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        LOGGER.info("Generating dynamic {} for BuddingCrystals", typeName);

        stopwatch = Stopwatch.createStarted();
        buildResources(dataCache);
        this.namespaces = dataCache.keySet()
                .stream()
                .map(ResourceLocation::getNamespace)
                .collect(Collectors.toSet());
        stopwatch.stop();

        LOGGER.info("Generated dynamic {} in {}ms", typeName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... fileName) { return null; }

    @Override
    public final IoSupplier<InputStream> getResource(PackType type, ResourceLocation location)
    {
        if (type == this.type && dataCache.containsKey(location))
        {
            return supplierForPath(location);
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output)
    {
        if (type == this.type)
        {
            dataCache.keySet()
                    .stream()
                    .filter(loc -> loc.getNamespace().equals(namespace))
                    .filter(loc -> loc.getPath().startsWith(path))
                    .forEach(loc -> output.accept(loc, supplierForPath(loc)));
        }
    }

    private IoSupplier<InputStream> supplierForPath(ResourceLocation loc)
    {
        return () -> new ByteArrayInputStream(dataCache.get(loc).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public final Set<String> getNamespaces(PackType type) { return type == this.type ? namespaces : Set.of(); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer)
    {
        if (deserializer == PackMetadataSection.TYPE)
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
