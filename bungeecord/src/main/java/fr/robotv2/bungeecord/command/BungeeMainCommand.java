package fr.robotv2.bungeecord.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.bungeecord.util.UUIDFetcher;
import fr.robotv2.common.reset.ResetService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("rtq-bunge|robottimedquest-bungee")
public class BungeeMainCommand extends BaseCommand {

    private final RTQBungeePlugin plugin;

    public BungeeMainCommand(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("robottimedquest.command.reload")
    public void onReload(CommandSender sender) {
        plugin.onReload();
        sender.sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("reset")
    @CommandPermission("robottimedquest.command.reset")
    @CommandCompletion("@players @services")
    public void onReset(CommandSender sender, String playerName, @Optional ResetService service) {
        UUIDFetcher.getUUID(playerName, !plugin.isOnlineMode()).thenAccept(uuid -> {

            if(uuid == null) {
                sender.sendMessage(ChatColor.RED + "This player does not exist.");
                return;
            }

            plugin.getBungeeResetPublisher().reset(uuid, service != null ? service.getId() : null);
            sender.sendMessage(ChatColor.GREEN + "The player has been reinitialized successfully. ");
        });
    }
}
