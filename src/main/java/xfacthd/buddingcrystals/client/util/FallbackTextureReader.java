package xfacthd.buddingcrystals.client.util;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.lukebemish.dynamic_asset_generator.client.api.json.ITexSource;
import io.github.lukebemish.dynamic_asset_generator.client.util.ImageUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.function.Supplier;

public class FallbackTextureReader implements ITexSource
{
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public Supplier<NativeImage> getSupplier(String input) throws JsonSyntaxException
    {
        LocationSource source = GSON.fromJson(input, LocationSource.class);

        ResourceLocation tex = ResourceLocation.of(source.path, ':');
        ResourceLocation texPath = texPath(tex);
        ResourceLocation fallback = ResourceLocation.of(source.fallback, ':');
        ResourceLocation fallbackPath = texPath(fallback);

        return () ->
        {
            try
            {
                return ImageUtils.getImage(texPath);
            }
            catch (IOException ex)
            {
                try
                {
                    LOGGER.debug("Issue loading main texture: {}, trying fallback", tex);
                    return ImageUtils.getImage(fallbackPath);
                }
                catch (IOException ex2)
                {
                    LOGGER.error("Issue loading texture: {}", fallback);
                    return null;
                }
            }
        };
    }

    private static ResourceLocation texPath(ResourceLocation loc)
    {
        return new ResourceLocation(loc.getNamespace(), "textures/" + loc.getPath() + ".png");
    }

    @SuppressWarnings("unused")
    public static class LocationSource
    {
        @Expose
        String source_type;
        @Expose
        public String path;
        @Expose
        public String fallback;
    }
}
