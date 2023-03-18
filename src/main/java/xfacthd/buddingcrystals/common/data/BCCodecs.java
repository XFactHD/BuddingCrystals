package xfacthd.buddingcrystals.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public class BCCodecs
{
    public static Codec<Integer> intMin(int minExclusive)
    {
        final Function<Integer, DataResult<Integer>> checker = checkMin(minExclusive);
        return Codec.INT.flatXmap(checker, checker);
    }

    public static Codec<Float> floatMin(float minExclusive)
    {
        final Function<Float, DataResult<Float>> checker = checkMin(minExclusive);
        return Codec.FLOAT.flatXmap(checker, checker);
    }

    public static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkMin(final N minExclusive)
    {
        return value ->
        {
            if (value.compareTo(minExclusive) > 0)
            {
                return DataResult.success(value);
            }
            return DataResult.error(() -> "Value " + value + " is lower than or equal to " + minExclusive, value);
        };
    }
}
