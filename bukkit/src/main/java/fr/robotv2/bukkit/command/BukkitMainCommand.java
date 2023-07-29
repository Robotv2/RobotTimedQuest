package fr.robotv2.bukkit.command;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
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

    @DefaultFor({"rtq", "robottimedquest"})
    public void onDefault(BukkitCommandActor actor) {
        actor.getSender().sendMessage(ChatColor.GREEN + "This server is using RobotTimedQuest with version " + plugin.getDescription().getVersion() + ".");
    }

    @Subcommand("reload")
    @Usage("reload")
    @CommandPermission("robottimedquest.command.reload")
    public void onReload(BukkitCommandActor actor) {
        plugin.onReload();
        actor.getSender().sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("reset")
    @Usage("reset <player> [<reset_id>]")
    @CommandPermission("robottimedquest.command.reset")
    @AutoComplete("@players @services")
    public void onReset(BukkitCommandActor actor, OfflinePlayer offlinePlayer, @Optional ResetService service) {

        if(!offlinePlayer.isOnline() && plugin.isBungeecordMode()) {
            actor.getSender().sendMessage(ChatColor.RED + "Please use the bungeecord command to reset an offline player.");
            return;
        }

        final UUID targetUniqueId = offlinePlayer.getUniqueId();
        final String resetId = service != null ? service.getId() : null;

        plugin.getResetPublisher().reset(targetUniqueId, resetId);
        actor.getSender().sendMessage(ChatColor.GREEN + "The player has been reinitialized successfully. ");
    }

    @Subcommand("quests")
    @Usage("quests")
    @CommandPermission("robottimedquest.command.quests")
    public void onQuests(BukkitCommandActor actor) {
        plugin.getGuiHandler().openMenu(actor.requirePlayer());
    }
}
