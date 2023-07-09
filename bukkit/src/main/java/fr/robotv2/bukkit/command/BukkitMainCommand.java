package fr.robotv2.bukkit.command;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import revxrsal.commands.annotation.*;
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
        actor.getSender().sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("reset")
    @Usage("reset <player> [<reset_id>]")
    @CommandPermission("robottimedquest.command.reset")
    @AutoComplete("@players @services")
    public void onReset(BukkitCommandActor actor, OfflinePlayer offlinePlayer, @Optional ResetService service) {

        if(plugin.isBungeecordMode()) {
            actor.getSender().sendMessage(ChatColor.RED + "Please, use the bungeecord command to reset a player.");
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

    @Subcommand("debug")
    @Usage("debug")
    @CommandPermission("robottimedquest.command.debug")
    public void onDebug(BukkitCommandActor actor) {
        actor.getSender().sendMessage("STARTING DEBUG");
        for(ActiveQuest activeQuest : QuestPlayer.getQuestPlayer(actor.requirePlayer().getUniqueId()).getActiveQuests()) {
            actor.getSender().sendMessage(activeQuest.getQuestId());
        }
        actor.getSender().sendMessage("END DEBUG");
    }
}
