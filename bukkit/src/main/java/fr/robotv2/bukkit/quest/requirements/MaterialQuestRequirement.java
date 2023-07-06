package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class MaterialQuestRequirement extends QuestRequirement<Material> {

    private final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    private boolean all = false;

    public MaterialQuestRequirement(Quest quest) {
        super(quest);

        final List<String> materialStrings = quest.getSection().getStringList("required_targets");

        if(materialStrings.contains("*")) {
            all = true;
            return;
        }

        for(String materialString : materialStrings) {

            final Material material = Material.matchMaterial(materialString);

            if(material == null) {
                RTQBukkitPlugin.getInstance().getLogger().warning(ChatColor.RED + materialString + " isn't a valid material name.");
                continue;
            }

            materials.add(material);
        }
    }

    @Override
    public Class<? extends Material> classGeneric() {
        return Material.class;
    }

    @Override
    public boolean isTarget(@NotNull Material material) {
        return all || materials.contains(material);
    }
}
