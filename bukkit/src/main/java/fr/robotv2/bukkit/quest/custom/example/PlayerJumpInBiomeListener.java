package fr.robotv2.bukkit.quest.custom.example;

import fr.robotv2.bukkit.RobotTimedQuestAPI;
import fr.robotv2.bukkit.quest.custom.CustomQuestProgressionEnhancer;
import org.bukkit.Statistic;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public class PlayerJumpInBiomeListener extends CustomQuestProgressionEnhancer<Biome> {

    public PlayerJumpInBiomeListener() {
        RobotTimedQuestAPI.registerCustomType(new JumpCustomType());
    }

    @EventHandler
    public void onJump(PlayerStatisticIncrementEvent event) {

        if(event.getStatistic() != Statistic.JUMP) {
            return;
        }

        final Player player = event.getPlayer();
        final Biome biome = player.getLocation().getBlock().getBiome();

        this.trigger(player, "JUMP", biome, event, event.getNewValue() - event.getPreviousValue());
    }
}
