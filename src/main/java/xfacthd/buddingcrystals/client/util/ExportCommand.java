package xfacthd.buddingcrystals.client.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.*;
import net.minecraft.network.chat.TranslatableComponent;
import org.slf4j.Logger;
import xfacthd.buddingcrystals.BuddingCrystals;
import xfacthd.buddingcrystals.common.BCContent;
import xfacthd.buddingcrystals.common.util.CrystalLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;

public final class ExportCommand
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MSG_NO_SUCH_CRYSTAL = "commands." + BuddingCrystals.MOD_ID + ".no_such_crystal";
    public static final String MSG_EXPORT_ERROR = "commands." + BuddingCrystals.MOD_ID + ".export_error";
    public static final String MSG_CRYSTALS_EXPORTED = "commands." + BuddingCrystals.MOD_ID + ".crystals_exported";
    public static final String MSG_CRYSTALS_OVERWRITTEN = "commands." + BuddingCrystals.MOD_ID + ".crystals_overwritten";
    public static final String MSG_CRYSTAL_EXPORTED = "commands." + BuddingCrystals.MOD_ID + ".crystal_exported";
    public static final String MSG_CRYSTAL_OVERWRITTEN = "commands." + BuddingCrystals.MOD_ID + ".crystal_overwritten";
    public static final String MSG_CRYSTAL_EXISTS = "commands." + BuddingCrystals.MOD_ID + ".crystal_exists";

    private static final DynamicCommandExceptionType NO_SUCH_CRYSTAL = new DynamicCommandExceptionType(
            crystal -> new TranslatableComponent(MSG_NO_SUCH_CRYSTAL, crystal)
    );
    private static final Dynamic2CommandExceptionType EXPORT_ERROR = new Dynamic2CommandExceptionType(
            (name, error) -> new TranslatableComponent(MSG_EXPORT_ERROR, name, error)
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal(BuddingCrystals.MOD_ID)
                .then(Commands.literal("export_all")
                        .executes(stack -> exportAll(stack, false))
                        .then(Commands.argument("overwrite_existing", BoolArgumentType.bool())
                                .executes(stack -> exportAll(stack, true))
                        )
                )
                .then(Commands.literal("export")
                        .then(Commands.argument("crystal", StringArgumentType.string())
                                .suggests(ExportCommand::getCrystalSuggestions)
                                .executes(stack -> exportSpecific(stack, false))
                                .then(Commands.argument("overwrite_existing", BoolArgumentType.bool())
                                        .executes(stack -> exportSpecific(stack, true))
                                )
                        )
                )
        );
    }

    private static int exportAll(CommandContext<CommandSourceStack> ctx, boolean withOverwrite) throws CommandSyntaxException
    {
        boolean overwrite = withOverwrite && BoolArgumentType.getBool(ctx, "overwrite_existing");

        int total = BCContent.builtinNames().size();
        int count = 0;
        int overwritten = 0;

        for (String name : BCContent.builtinNames())
        {
            Result res = exportCrystalDefinition(name, overwrite);
            if (res != Result.EXISTS)
            {
                count++;
                if (res == Result.OVERWRITTEN)
                {
                    overwritten++;
                }
            }
        }

        if (overwrite)
        {
            ctx.getSource().sendSuccess(new TranslatableComponent(MSG_CRYSTALS_OVERWRITTEN, count, total, overwritten), true);
        }
        else
        {
            ctx.getSource().sendSuccess(new TranslatableComponent(MSG_CRYSTALS_EXPORTED, count, total, total - count), true);
        }
        return count;
    }

    private static int exportSpecific(CommandContext<CommandSourceStack> ctx, boolean withOverwrite) throws CommandSyntaxException
    {
        boolean overwrite = withOverwrite && BoolArgumentType.getBool(ctx, "overwrite_existing");
        String crystal = StringArgumentType.getString(ctx, "crystal");

        if (!BCContent.isBuiltin(crystal))
        {
            throw NO_SUCH_CRYSTAL.create(crystal);
        }

        Result result = exportCrystalDefinition(crystal, overwrite);
        if (result == Result.EXISTS)
        {
            ctx.getSource().sendFailure(new TranslatableComponent(MSG_CRYSTAL_EXISTS, crystal));
            return 0;
        }
        else if (result == Result.EXPORTED)
        {
            ctx.getSource().sendSuccess(new TranslatableComponent(MSG_CRYSTAL_EXPORTED, crystal), true);
        }
        else
        {
            ctx.getSource().sendSuccess(new TranslatableComponent(MSG_CRYSTAL_OVERWRITTEN, crystal), true);
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> getCrystalSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder)
    {
        return SharedSuggestionProvider.suggest(BCContent.builtinNames(), builder);
    }

    private static Result exportCrystalDefinition(String name, boolean overwrite) throws CommandSyntaxException
    {
        Path path = CrystalLoader.CRYSTAL_PATH.resolve(name + ".json");

        boolean exists = Files.exists(path);
        if (exists && !overwrite)
        {
            return Result.EXISTS;
        }

        String json = CrystalLoader.export(name);
        try
        {
            Files.writeString(path, json, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e)
        {
            LOGGER.error("Encountered an error while exporting builtin crystal definition '" + name + "'", e);
            throw EXPORT_ERROR.create(name, e.getMessage());
        }

        return exists ? Result.OVERWRITTEN : Result.EXPORTED;
    }

    private enum Result
    {
        EXISTS,
        EXPORTED,
        OVERWRITTEN
    }
}
