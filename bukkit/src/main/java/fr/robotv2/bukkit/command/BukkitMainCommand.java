package fr.robotv2.bukkit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.QuestDoneEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@CommandAlias("rtq|robottimedquest")
public class BukkitMainCommand extends BaseCommand {

    private final RTQBukkitPlugin plugin;

    public BukkitMainCommand(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Default("rtq|robottimedquest")
    public void onDefault(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "This server is using RobotTimedQuest with version " + plugin.getDescription().getVersion() + ".");
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
    public void onReset(CommandSender sender, OfflinePlayer offlinePlayer, @Optional ResetService service) {

        final UUID targetUniqueId = offlinePlayer.getUniqueId();
        final String resetId = service != null ? service.getId() : null;

        this.plugin.debug("RESET-ID -> " + (resetId != null ? resetId : "NULL"));

        plugin.getResetPublisher().reset(targetUniqueId, resetId);

        if(plugin.isBungeecordMode()) {
            sender.sendMessage(ChatColor.GREEN + "The reset's request has been sent to bungeecord");
            sender.sendMessage(ChatColor.GREEN + "Please check bungeecoord's console to make sure nothing wrong happened.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "The player has been reinitialized successfully.");
        }
    }

    @Subcommand("quests")
    @CommandPermission("robottimedquest.command.quests")
    public void onQuests(CommandSender sender) {
        if(sender instanceof Player) {
            plugin.getGuiHandler().openMenu((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Can't do that from console.");
        }
    }

    @Subcommand("complete")
    @CommandPermission("robottimedquest.command.complete")
    @CommandCompletion("@players @target_quests")
    public void onComplete(CommandSender sender, OfflinePlayer offlinePlayer, String resetId) {

        final Player player = offlinePlayer.getPlayer();
        final QuestPlayer questPlayer;

        if(player == null || (questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId())) == null) {
            sender.sendMessage(ChatColor.RED + "Player must be online and on the same server than you to do that.");
            return;
        }

        final List<ActiveQuest> activeQuests = questPlayer.getActiveQuests();
        final ActiveQuest activeQuest = activeQuests.stream()
                .filter(current -> current.getQuestId().equals(resetId))
                .findFirst().orElse(null);

        if(activeQuest == null) {
            sender.sendMessage(ChatColor.RED + "Invalid id: the target is not currently doing this quest.");
            return;
        }

        if(activeQuest.isDone()) {
            sender.sendMessage(ChatColor.RED + "This quest is already done.");
            return;
        }

        final Quest quest = this.plugin.getQuestManager().fromId(activeQuest.getQuestId());

        if(quest != null) {
            new StringListProcessor().process(player, quest);
        }

        activeQuest.setDone(true);
        Bukkit.getPluginManager().callEvent(new QuestDoneEvent(activeQuest));
    }
}
