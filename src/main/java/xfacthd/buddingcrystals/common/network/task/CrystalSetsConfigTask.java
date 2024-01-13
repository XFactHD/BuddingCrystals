package xfacthd.buddingcrystals.common.network.task;

import com.google.common.collect.Sets;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.network.payload.AckCrystalSetsPayload;
import xfacthd.buddingcrystals.common.network.payload.SyncCrystalSetsPayload;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// This task MUST run before NeoForge syncs registries, so we can disconnect on mismatch and prevent unnecessary sync
// of unmatched registries. This is achieved via dependency ordering
public final class CrystalSetsConfigTask implements ICustomConfigurationTask
{
    public static final Type TYPE = new Type(BuddingCrystals.rl("crystal_sets"));
    public static final String MSG_ADDITIONAL_SETS_SERVER = "msg.config." + BuddingCrystals.MOD_ID + ".additional_sets_server";
    public static final String MSG_ADDITIONAL_SETS_CLIENT = "msg.config." + BuddingCrystals.MOD_ID + ".additional_sets_client";
    public static final Component MSG_CHECK_FILES_MATCH = Component.translatable("msg.config." + BuddingCrystals.MOD_ID + ".check_files_match");

    @Override
    public void run(Consumer<CustomPacketPayload> sender)
    {
        sender.accept(new SyncCrystalSetsPayload(getCrystalEntries()));
    }

    @Override
    public Type type()
    {
        return TYPE;
    }

    public static void handleSync(SyncCrystalSetsPayload payload, ConfigurationPayloadContext ctx)
    {
        Set<SyncCrystalSetsPayload.Entry> remoteSet = payload.crystalEntries();
        Set<SyncCrystalSetsPayload.Entry> localSet = getCrystalEntries();

        List<Component> messages = new ArrayList<>(2);

        Set<SyncCrystalSetsPayload.Entry> diffOne = Sets.difference(remoteSet, localSet);
        if (!diffOne.isEmpty())
        {
            messages.add(formatDisconnectMessage(MSG_ADDITIONAL_SETS_SERVER, diffOne));
        }

        Set<SyncCrystalSetsPayload.Entry> diffTwo = Sets.difference(localSet, remoteSet);
        if (!diffTwo.isEmpty())
        {
            messages.add(formatDisconnectMessage(MSG_ADDITIONAL_SETS_CLIENT, diffTwo));
        }

        if (!messages.isEmpty())
        {
            MutableComponent message = Component.literal("[BuddingCrystals]\n\n");
            messages.forEach(msg -> message.append(msg).append(CommonComponents.NEW_LINE));
            message.append(CommonComponents.NEW_LINE).append(MSG_CHECK_FILES_MATCH);
            ctx.packetHandler().disconnect(message);
            return;
        }

        ctx.replyHandler().send(new AckCrystalSetsPayload());
    }

    private static Component formatDisconnectMessage(String langKey, Set<SyncCrystalSetsPayload.Entry> diff)
    {
        String diffString = diff.stream().map(SyncCrystalSetsPayload.Entry::name).collect(Collectors.joining(", "));
        return Component.translatable(langKey, diffString);
    }

    private static Set<SyncCrystalSetsPayload.Entry> getCrystalEntries()
    {
        return BCContent.allActiveSets()
                .stream()
                .map(set -> new SyncCrystalSetsPayload.Entry(set.getName(), set.getCompatMod(), set.isActive()))
                .collect(Collectors.toCollection(HashSet::new));
    }
}
