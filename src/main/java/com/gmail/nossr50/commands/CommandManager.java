package com.gmail.nossr50.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import com.gmail.nossr50.commands.chat.AdminChatCommand;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/*
 * For now this class will only handle ACF converted commands, all other commands will be handled elsewhere
 */
public class CommandManager {
    public static final String ADMIN_CONDITION = "adminCondition";
    public static final String PARTY_CONDITION = "partyCondition";
    public static final String MMO_DATA_LOADED = "mmoDataLoaded";

    private final @NotNull mcMMO pluginRef;
    private final @NotNull BukkitCommandManager bukkitCommandManager;

    public CommandManager(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        bukkitCommandManager = new BukkitCommandManager(pluginRef);

        registerConditions();
        registerCommands();
    }

    public void registerConditions() {
        // Method or Class based - Can only be used on methods
        bukkitCommandManager.getCommandConditions().addCondition(ADMIN_CONDITION, (context) -> {
            BukkitCommandIssuer issuer = context.getIssuer();

            if(issuer.getIssuer() instanceof Player) {
                validateAdmin(issuer.getPlayer());
            }
        });

        bukkitCommandManager.getCommandConditions().addCondition(MMO_DATA_LOADED, (context) -> {
            BukkitCommandIssuer bukkitCommandIssuer = context.getIssuer();

            if(bukkitCommandIssuer.getIssuer() instanceof Player) {
                validateLoadedData(bukkitCommandIssuer.getPlayer());
            }
        });

        bukkitCommandManager.getCommandConditions().addCondition(PARTY_CONDITION, (context) -> {
            BukkitCommandIssuer bukkitCommandIssuer = context.getIssuer();

            if(bukkitCommandIssuer.getIssuer() instanceof Player) {
                validateLoadedData(bukkitCommandIssuer.getPlayer());
                validatePlayerParty(bukkitCommandIssuer.getPlayer());
            }
        });
    }

    private void registerCommands() {
        bukkitCommandManager.registerCommand(new AdminChatCommand(pluginRef));
        bukkitCommandManager.registerCommand(new PartyChatCommand(pluginRef));
    }


    public void validateAdmin(@NotNull Player player) {
        if(!player.isOp() && !Permissions.adminChat(player)) {
            throw new ConditionFailedException("You are lacking the correct permissions to use this command.");
        }
    }

    public void validateLoadedData(@NotNull Player player) {
        if(UserManager.getPlayer(player) == null) {
            throw new ConditionFailedException("Your mcMMO player data has not yet loaded!");
        }
    }

    public void validatePlayerParty(@NotNull Player player) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if(mmoPlayer.getParty() == null) {
            throw new ConditionFailedException(LocaleLoader.getString("Commands.Party.None"));
        }
    }

    public @NotNull BukkitCommandManager getBukkitCommandManager() {
        return bukkitCommandManager;
    }
}
