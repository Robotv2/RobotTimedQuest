package fr.robotv2.bungeecord.command;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.bungeecord.util.UUIDFetcher;
import fr.robotv2.common.reset.ResetService;
import net.md_5.bungee.api.ChatColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bungee.BungeeCommandActor;
import revxrsal.commands.bungee.annotation.CommandPermission;

@Command({"rtq-bungee", "robottimedquest-bungee"})
public class BungeeMainCommand {

    private final RTQBungeePlugin plugin;

    public BungeeMainCommand(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @Usage("reload")
    @CommandPermission("robottimedquest.command.reload")
    public void onReload(BungeeCommandActor actor) {
        plugin.onReload();
        actor.reply(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("reset")
    @Usage("reset <player> [<reset_id>]")
    @CommandPermission("robottimedquest.command.reset")
    public void onReset(BungeeCommandActor actor, String playerName, @Optional ResetService service) {
        UUIDFetcher.getUUID(playerName, !plugin.isOnlineMode()).thenAccept(uuid -> {

            if(uuid == null) {
                actor.reply(ChatColor.RED + "This player doesn't exist.");
                return;
            }

            plugin.getBungeeResetPublisher().reset(uuid, service != null ? service.getId() : null);
            actor.reply(ChatColor.GREEN + "The player has been reinitialized successfully. ");
        });
    }
}
