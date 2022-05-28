package xfacthd.buddingcrystals.common.datagen.util;

import io.github.lukebemish.dynamic_asset_generator.datagen.ImageSource;
import io.github.lukebemish.dynamic_asset_generator.datagen.TextureConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TrackingTextureConfig
{
    private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

    private final TextureConfig config;
    private final ExistingFileHelper fileHelper;

    public TrackingTextureConfig(TextureConfig config, ExistingFileHelper fileHelper)
    {
        this.config = config;
        this.fileHelper = fileHelper;
    }

    public TrackingTextureConfig input(ImageSource input)
    {
        config.input(input);
        return this;
    }

    public TrackingTextureConfig output(ResourceLocation output)
    {
        config.output(output);
        fileHelper.trackGenerated(output, TEXTURE);
        return this;
    }
}
