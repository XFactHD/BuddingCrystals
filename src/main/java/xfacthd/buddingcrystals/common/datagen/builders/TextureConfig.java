package xfacthd.buddingcrystals.common.datagen.builders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public final class TextureConfig
{
    private ImageSource input;
    private ResourceLocation output;

    public TextureConfig() { }

    public TextureConfig input(ImageSource input)
    {
        this.input = input;
        return this;
    }

    public TextureConfig output(ResourceLocation output)
    {
        Preconditions.checkNotNull(output, "Output texture must not be null");

        this.output = output;
        return this;
    }

    JsonObject toJson()
    {
        Preconditions.checkNotNull(input, "No input set");
        Preconditions.checkNotNull(output, "No output set");

        JsonObject object = new JsonObject();
        object.addProperty("output_location", output.toString());
        object.add("input", input.toJson());
        return object;
    }
}
