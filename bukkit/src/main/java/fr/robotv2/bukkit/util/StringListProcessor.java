package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class StringListProcessor {

    public void process(Player player, List<String> rewards) {

        for (String reward : rewards) {

            final String prefix = reward.split(" ")[0];

            reward = reward.length() == prefix.length() ? reward.trim() : reward.substring(prefix.length() + 1).trim();
            reward = PlaceholderUtil.parsePlaceholders(player, reward);

            switch (prefix) {

                case "[CLOSE]":
                    player.closeInventory();
                    break;

                case "[CONSOLE]":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward);
                    break;

                case "[PLAYER]":
                    Bukkit.dispatchCommand(player, reward);
                    break;

                case "[MONEY]":
                    final double bal = Double.parseDouble(reward);
                    // VaultAPI.giveMoney(player, bal); // TODO
                    break;

                case "[EXP_LEVEL]":
                    final int level = Integer.parseInt(reward);
                    player.giveExpLevels(level);
                    break;

                case "[EXP_POINTS]":
                    final int points = Integer.parseInt(reward);
                    player.giveExp(points);
                    break;

                case "[MESSAGE]":
                    final String message = ColorUtil.color(reward);
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
