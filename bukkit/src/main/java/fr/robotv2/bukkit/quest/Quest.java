package fr.robotv2.bukkit.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;
import fr.robotv2.bukkit.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Quest {

    private final static RTQBukkitPlugin PLUGIN = RTQBukkitPlugin.getInstance();

    private final ConfigurationSection section;
    private final String id;

    private final String name;
    private final List<String> description;
    private final Material material;

    private final String resetId;
    private final QuestType type;

    private final int requiredAmount;
    private final List<String> rewards;

    private final QuestRequirement<?> questRequirement;
    private final List<Condition<?>> conditions = new ArrayList<>();

    public Quest(ConfigurationSection section) {
        this.section = section;

        this.id = section.getName();
        this.name = section.getString("name");
        this.description = section.getStringList("description");

        final String materialString = section.getString("menu_item", "BOOK");
        this.material = Objects.requireNonNull(Material.matchMaterial(materialString), materialString + " isn't a valid material.");

        this.resetId = Objects.requireNonNull(section.getString("reset_id"));
        this.type = QuestType.getByName(section.getString("quest_type"));

        this.requiredAmount = section.getInt("required_amount", 0);
        this.rewards = section.getStringList("rewards");

        if(type == null || type.getQuestRequirementConstant() == null) {
            this.questRequirement = null;
        } else {
            this.questRequirement = type.getQuestRequirementConstant().toInstance(this);
        }

        final ConfigurationSection conditionSection = section.getConfigurationSection("conditions");
        if(conditionSection != null) {
            this.loadConditions(conditionSection);
        }
    }

    public ConfigurationSection getSection() {
        return this.section;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public Material getMaterial() {
        return this.material;
    }

    public ItemStack getGuiItem(int progress) {

        final ItemStack itemStack = new ItemStack(this.getMaterial());
        final ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        final List<String> description = new ArrayList<>(this.description);

        meta.setDisplayName(ColorUtil.color(this.name));
        description.add(" ");

        if(type.isNumerical()) {
            if (progress >= requiredAmount) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
                description.add("&aYou have successfully done this quest.");
            } else {
                description.add("&7Progress: &e" + progress + "&8/&e" + this.requiredAmount);
            }
        } else {
            if(progress >= 1) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
                description.add("&aYou have successfully done this quest.");
            } else {
                description.add("&cThis quest is not done yet.");
            }
        }

        meta.setLore(description.stream().map(ColorUtil::color).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public QuestType getType() {
        return this.type;
    }

    public String getResetId() {
        return this.resetId;
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    @Nullable
    public QuestRequirement<?> getQuestRequirement() {
        return this.questRequirement;
    }

    private void loadConditions(@NotNull ConfigurationSection conditionSection) {
        for(String key : conditionSection.getKeys(false)) {
            final Optional<Condition<?>> optional = PLUGIN.getConditionManager().toInstance(key, conditionSection);
            optional.ifPresent(conditions::add);
        }
    }

    public List<Condition<?>> getConditions() {
        return this.conditions;
    }

    public boolean isTarget(Object object) {
        final QuestRequirement<?> questRequirement = getQuestRequirement();
        if(questRequirement == null) return true;
        return questRequirement.isTarget0(object);
    }

    @Override
    public boolean equals(Object otherObject) {

        if (otherObject == this) {
            return true;
        }

        if (!(otherObject instanceof Quest)) {
            return false;
        }

        final Quest otherQuest = (Quest) otherObject;
        return Objects.equals(this.id, otherQuest.id);
    }
}