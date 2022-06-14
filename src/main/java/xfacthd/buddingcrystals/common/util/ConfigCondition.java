package xfacthd.buddingcrystals.common.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import xfacthd.buddingcrystals.BuddingCrystals;

public final class ConfigCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(BuddingCrystals.MOD_ID, "config");
    private final String config;

    public ConfigCondition(String config) { this.config = config; }

    @Override
    public ResourceLocation getID() { return NAME; }

    @Override
    public boolean test(IContext context) { return CommonConfig.isEnabled(config); }

    @Override
    @SuppressWarnings("removal")
    public boolean test() { return false; }

    public static class Serializer implements IConditionSerializer<ConfigCondition>
    {
        @Override
        public void write(JsonObject json, ConfigCondition value)
        {
            json.addProperty("config", value.config);
        }

        @Override
        public ConfigCondition read(JsonObject json)
        {
            return new ConfigCondition(GsonHelper.getAsString(json, "config"));
        }

        @Override
        public ResourceLocation getID() { return NAME; }
    }
}
