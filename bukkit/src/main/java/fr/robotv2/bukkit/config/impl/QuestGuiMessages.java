package fr.robotv2.bukkit.config.impl;

import fr.robotv2.bukkit.config.FileUpdator;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestGuiMessages implements FileUpdator {

    public final static String PREFIX = "quest-gui.messages";

    @Override
    public boolean reason(YamlConfiguration configuration) {
        return !configuration.isSet(PREFIX);
    }

    @Override
    public void update(YamlConfiguration configuration) {
        configuration.set(PREFIX + ".quest_progression", "&7Progress: &e%quest_progression% &8/ &e%quest_required%");
        configuration.set(PREFIX + ".quest_not_done", "&cThis quest is not done yet.");
        configuration.set(PREFIX + ".quest_done", "&aYou have successfully done this quest.");
    }
}
