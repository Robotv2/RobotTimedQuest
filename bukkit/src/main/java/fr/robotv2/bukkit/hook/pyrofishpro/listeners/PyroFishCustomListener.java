package fr.robotv2.bukkit.hook.pyrofishpro.listeners;

import fr.robotv2.bukkit.events.PyroFishCaughtEvent;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.custom.CustomQuestProgressionEnhancer;
import org.bukkit.event.EventHandler;

public class PyroFishCustomListener extends CustomQuestProgressionEnhancer<String> {

    @EventHandler
    public void onPyroFishCaught(PyroFishCaughtEvent event) {
        final PyroFishProHook.PyroFishWrapper wrapper = event.getFishWrapper();
        this.trigger(event.getPlayer(), "PYRO_FISH", (wrapper.tier + ":" + wrapper.fishnumber), event, event.getEntity().getItemStack().getAmount());
    }
}
