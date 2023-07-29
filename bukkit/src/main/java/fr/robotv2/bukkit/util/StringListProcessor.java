package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.VaultHook;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class StringListProcessor {

    public void process(Player player, List<String> rewards) {

        for (String reward : rewards) {

            final String prefix = reward.split(" ")[0];

            String argument = reward.length() == prefix.length() ? reward.trim() : reward.substring(prefix.length() + 1).trim();

            argument = PlaceholderUtil.PLAYER_PLACEHOLDER.parse(player, argument);
            argument = PlaceholderUtil.parsePlaceholders(player, argument);

            switch (prefix) {

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
                    if(!Hooks.isVaultEnabled()) {
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
                    final String message = ColorUtil.color(argument);
                    player.sendMessage(message);
                    break;

                default:
                    throw new IllegalArgumentException(prefix + " isn't a valid prefix.");
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
