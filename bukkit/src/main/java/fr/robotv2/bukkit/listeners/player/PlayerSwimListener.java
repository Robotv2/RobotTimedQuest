package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.PlayerSwimEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;

public class PlayerSwimListener extends QuestProgressionEnhancer<Void> {

    public PlayerSwimListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onSwim(PlayerSwimEvent event) {
        this.incrementProgression(event.getPlayer(), QuestType.SWIM, null, event, (event.getNewValue() - event.getBeforeValue()));
    }
}
