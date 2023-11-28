package xfacthd.buddingcrystals.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public final class ConfigCondition implements ICondition
{
    public static final Codec<ConfigCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("config").forGetter(cond -> cond.config)
    ).apply(inst, ConfigCondition::new));

    private final String config;

    public ConfigCondition(String config)
    {
        this.config = config;
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(IContext context)
    {
        return CommonConfig.isEnabled(config);
    }
}
