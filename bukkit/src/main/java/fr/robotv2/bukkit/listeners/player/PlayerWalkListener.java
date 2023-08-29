package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.PlayerWalkEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerWalkListener extends QuestProgressionEnhancer<Void> {

    public PlayerWalkListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWalk(PlayerWalkEvent event) {
        this.incrementProgression(event.getPlayer(), QuestType.WALK, null, event, (event.getNewValue() - event.getBeforeValue()));
    }
}
