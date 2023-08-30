package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.PlayerWalkEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;

public class PlayerWalkListener extends QuestProgressionEnhancer<Void> {

    public PlayerWalkListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWalk(PlayerWalkEvent event) {
        this.incrementProgression(event.getPlayer(), QuestType.WALK, null, event, (event.getNewValue() - event.getBeforeValue()));
    }
}
