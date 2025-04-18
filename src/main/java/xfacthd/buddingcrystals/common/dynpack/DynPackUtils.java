package xfacthd.buddingcrystals.common.dynpack;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class DynPackUtils
{
    @Nullable
    public static <T> String toConditionalJson(Codec<Optional<WithConditions<T>>> codec, T value, ICondition... conditions)
    {
        DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, Optional.of(new WithConditions<>(value, conditions)));
        return result.isSuccess() ? result.getOrThrow().toString() : null;
    }

    @Nullable
    public static <T> String toJson(Codec<T> codec, DynamicOps<JsonElement> ops, T value)
    {
        DataResult<JsonElement> result = codec.encodeStart(ops, value);
        return result.isSuccess() ? result.getOrThrow().toString() : null;
    }

    public static IoSupplier<InputStream> toIoSupplier(String content)
    {
        return () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private DynPackUtils() { }
}
