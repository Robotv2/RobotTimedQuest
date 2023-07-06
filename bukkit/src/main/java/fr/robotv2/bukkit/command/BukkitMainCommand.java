package fr.robotv2.bukkit.command;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"rtq", "robottimedquest"})
public class BukkitMainCommand {

    private final RTQBukkitPlugin plugin;

    public BukkitMainCommand(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @Usage("reload")
    @CommandPermission("robottimedquest.command.reload")
    public void onReload(BukkitCommandActor actor) {
        plugin.onReload();
        actor.reply(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("reset")
    @Usage("reset <player> [<reset_id>]")
    @CommandPermission("robottimedquest.command.reset")
    public void onReset(BukkitCommandActor actor, OfflinePlayer offlinePlayer, @Optional ResetService service) {

        if(plugin.isBungeecordMode()) {
            actor.reply(ChatColor.RED + "Please, use the bungeecord command to reset a player.");
            return;
        }

        final UUID targetUniqueId = offlinePlayer.getUniqueId();
        final String resetId = service != null ? service.getId() : null;

        plugin.getResetPublisher().reset(targetUniqueId, resetId);
        actor.reply(ChatColor.GREEN + "The player has been reinitialized successfully. ");
    }
}
