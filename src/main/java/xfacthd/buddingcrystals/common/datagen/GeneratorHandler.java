package xfacthd.buddingcrystals.common.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.datagen.providers.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BuddingCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    private static final ExistingFileHelper.ResourceType TEXTURE_TYPE = new ExistingFileHelper.ResourceType(
            PackType.CLIENT_RESOURCES, ".png", "textures"
    );

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> holderProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        // Trick the generators into thinking the textures exist
        fileHelper.trackGenerated(bcRl("item/crystal_catalyst"), TEXTURE_TYPE);
        BCContent.builtinSets().forEach(set ->
        {
            String name = set.getName();
            fileHelper.trackGenerated(bcRl("block/budding/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/small_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/medium_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/large_bud/" + name), TEXTURE_TYPE);
            fileHelper.trackGenerated(bcRl("block/cluster/" + name), TEXTURE_TYPE);
        });

        gen.addProvider(true, buildPackMetadata(output, event.getModContainer().getModInfo()));
        gen.addProvider(event.includeClient(), new BuddingStateProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new BuddingLanguageProvider(output));
        gen.addProvider(event.includeServer(), new BuddingBlockTagsProvider(output, holderProvider, fileHelper));
        gen.addProvider(event.includeServer(), new BuddingLootTableProvider(output));
        gen.addProvider(event.includeServer(), new BuddingRecipeProvider(output, holderProvider));
    }

    private static PackMetadataGenerator buildPackMetadata(PackOutput output, IModInfo modInfo)
    {
        WorldVersion version = SharedConstants.getCurrentVersion();
        Map<PackType, Integer> packVersions = new EnumMap<>(PackType.class);
        int maxVersion = 0;
        for (PackType type : PackType.values())
        {
            int typeVersion = version.getPackVersion(type);
            packVersions.put(type, typeVersion);
            maxVersion = Math.max(maxVersion, typeVersion);
        }
        return new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal(modInfo.getDisplayName() + " resources"), maxVersion, packVersions
        ));
    }

    private static ResourceLocation bcRl(String path)
    {
        return new ResourceLocation(BuddingCrystals.MOD_ID, path);
    }



    private GeneratorHandler() { }
}
