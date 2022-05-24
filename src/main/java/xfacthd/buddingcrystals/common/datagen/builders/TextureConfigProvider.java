package xfacthd.buddingcrystals.common.datagen.builders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class TextureConfigProvider implements DataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

    private final DataGenerator generator;
    private final ExistingFileHelper fileHelper;
    private final String modid;
    private final Map<ResourceLocation, TextureConfig> generatedConfigs = new HashMap<>();

    public TextureConfigProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid)
    {
        this.generator = generator;
        this.fileHelper = fileHelper;
        this.modid = modid;
    }

    public abstract void addConfigs();

    public final TextureConfig config(String path)
    {
        return generatedConfigs.computeIfAbsent(
                new ResourceLocation(modid, path),
                loc -> new TextureConfig(fileHelper)
        );
    }

    public final ImageSource.File fileSource(ResourceLocation texture)
    {
        return new ImageSource.File(fileHelper, texture);
    }

    public final ImageSource.FallbackFile fallbackFileSource()
    {
        return new ImageSource.FallbackFile(fileHelper);
    }

    public final ImageSource.Color colorSource() { return new ImageSource.Color(fileHelper); }

    public final ImageSource.Overlay overlaySource() { return new ImageSource.Overlay(fileHelper); }

    public final ImageSource.Mask maskSource() { return new ImageSource.Mask(fileHelper); }

    public final ImageSource.Crop cropSource() { return new ImageSource.Crop(fileHelper); }

    public final ImageSource.Transform transformSource() { return new ImageSource.Transform(fileHelper); }

    public final ImageSource.CombinedPalettedImage combinedPalettedImageSource()
    {
        return new ImageSource.CombinedPalettedImage(fileHelper);
    }

    public final ImageSource.ForegroundTransfer foregroundTransferSource()
    {
        return new ImageSource.ForegroundTransfer(fileHelper);
    }

    @Override
    public final void run(HashCache cache)
    {
        generatedConfigs.clear();
        addConfigs();
        writeConfigs(cache);
    }

    private void writeConfigs(HashCache cache)
    {
        for (ResourceLocation location : generatedConfigs.keySet())
        {
            Path target = generator.getOutputFolder().resolve(String.format(
                    "assets/%s/dynamic_assets_sources/%s.json",
                    location.getNamespace(),
                    location.getPath()
            ));

            try
            {
                DataProvider.save(GSON, cache, generatedConfigs.get(location).toJson(), target);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getName() { return "palette_provider"; }
}
