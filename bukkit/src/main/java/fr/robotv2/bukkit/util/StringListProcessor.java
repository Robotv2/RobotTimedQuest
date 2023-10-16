package fr.robotv2.bukkit.util;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.vault.VaultHook;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StringListProcessor {

    public void process(Player player, List<String> rewards) {

        for (String reward : rewards) {

            final String prefix = reward.split(" ")[0];

            String argument = reward.length() == prefix.length() ? reward.trim() : reward.substring(prefix.length() + 1).trim();
            argument = PlaceholderUtil.withPlayerPlaceholders(player, argument);

            if(ColorUtil.hasColorFormat(argument)) {
                argument = ColorUtil.color(argument);
            }

            switch (prefix.toUpperCase(Locale.ROOT)) {

                case "[CLOSE]":
                    player.closeInventory();
                    break;

                case "[COMMAND]":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), argument);
                    break;

                case "[PLAYER]":
                    Bukkit.dispatchCommand(player, argument);
                    break;

                case "[MONEY]":
                    if(!Hooks.VAULT.isInitialized()) {
                       throw new IllegalStateException("Vault's missing. Please install vault in order to use the [MONEY] prefix");
                    }
                    final double bal = Double.parseDouble(argument);
                    VaultHook.giveMoney(player, bal);
                    break;

                case "[EXP_LEVEL]":
                    final int level = Integer.parseInt(argument);
                    player.giveExpLevels(level);
                    break;

                case "[EXP_POINTS]":
                    final int points = Integer.parseInt(argument);
                    player.giveExp(points);
                    break;

                case "[MESSAGE]":
                    player.sendMessage(argument);
                    break;

                case "[SOUND]":
                    final Sound sound = Enums.getIfPresent(Sound.class, argument.toUpperCase(Locale.ROOT)).orNull();
                    if(sound == null) {
                        RTQBukkitPlugin.getPluginLogger().warning("Unknown song: " + argument);
                        break;
                    }
                    player.playSound(player.getLocation(), sound, 1, 1);

                default:
                    RTQBukkitPlugin.getPluginLogger().log(Level.WARNING, String.format("'%s' is not a valid prefix.", prefix));
            }
        }
    }

    public void process(Player player, Quest quest) {

        if (player == null || quest == null) {
            return;
        }

        final List<String> rewards = quest.getRewards()
                .stream()
                .map(string -> PlaceholderUtil.QUEST_PLACEHOLDER.parse(quest, string))
                .collect(Collectors.toList());

        this.process(player, rewards);
    }
}
