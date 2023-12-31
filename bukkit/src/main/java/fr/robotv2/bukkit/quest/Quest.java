package fr.robotv2.bukkit.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.custom.CustomType;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;
import fr.robotv2.bukkit.quest.requirements.QuestRequirements;
import fr.robotv2.bukkit.util.item.HeadUtil;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Quest {

    private final static RTQBukkitPlugin PLUGIN = RTQBukkitPlugin.getInstance();

    private final ConfigurationSection section;
    private final String id;

    private final String name;
    private final List<String> description;
    private final Material material;
    private final int customModelData;

    // head
    @Nullable
    private String headTexture;
    @Nullable
    private String headOwner;

    private final String resetId;
    private final List<String> rewards;

    private final QuestType type;
    private final String customType;
    private QuestRequirement<?> questRequirement = null;
    private final List<Condition> conditions = new ArrayList<>();

    public Quest(ConfigurationSection section) {
        this.section = section;

        this.id = section.getName();
        this.name = section.getString("display");
        this.description = section.getStringList("description");

        final String materialString = section.getString("menu_item", "NULL");
        this.material = Objects.requireNonNull(Material.matchMaterial(materialString), "couldn't find a valid menu_item for: " + id);

        if(material == Material.PLAYER_HEAD) {
            this.headTexture = section.getString("head_texture");
            this.headOwner = section.getString("head_owner");
        }

        this.customModelData = section.getInt("custom_model_data", Integer.MIN_VALUE);

        this.resetId = Objects.requireNonNull(section.getString("reset_id"), "missing reset server for quest: " + id);
        this.type = Objects.requireNonNull(QuestType.getByName(section.getString("quest_type")), "missing type for quest: " + id);
        this.customType = section.getString("custom_type");
        this.rewards = section.getStringList("rewards");

        if(type.getRequiredClass() != null) {
            this.questRequirement = QuestRequirements.toQuestRequirement(type.getRequiredClass(), this);
        } else if(type == QuestType.CUSTOM) {
            final CustomType customType = PLUGIN.getCustomTypeManager().getCustomType(this.customType);
            if(customType != null) {
                this.questRequirement = customType.toQuestRequirement(this);
            }
        } else {
            this.questRequirement = null;
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

    public String getDisplay() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public Material getMaterial() {
        return this.material;
    }

    public CompletableFuture<ItemStack> getGuiItem(ActiveQuest activeQuest, OfflinePlayer offlinePlayer) {

        final Function<ItemStack, ItemStack> itemStackConsumer = itemStack -> {

            final ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
            final List<String> description = new ArrayList<>(this.description);

            meta.setDisplayName(
                    this.name != null && !this.name.isEmpty()
                            ? ColorUtil.color(this.name)
                            : this.name
            );

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);

            if(hasCustomModelData()) {
                meta.setCustomModelData(this.getCustomModelData());
            }

            description.add(" ");

            final ConfigurationSection guiMessages = PLUGIN.getGuiFile().getConfiguration().getConfigurationSection("quest-gui.messages");
            if(guiMessages != null) {

                String toAdd;

                if(activeQuest.isDone()) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
                    toAdd = guiMessages.getString("quest_done");
                } else if(this.isNumerical()) {
                    toAdd = guiMessages.getString("quest_progression");
                } else {
                    toAdd = guiMessages.getString("quest_not_done");
                }

                if(toAdd != null && !toAdd.trim().isEmpty()) {
                    description.add(toAdd);
                }
            }

            meta.setLore(description.stream()
                    .map(line -> !line.isEmpty() ? ColorUtil.color(line) : line)
                    .map(line -> PlaceholderUtil.PLAYER_PLACEHOLDER.parse(offlinePlayer, line))
                    .map(line -> PlaceholderUtil.QUEST_PLACEHOLDER.parse(this, line))
                    .map(line -> PlaceholderUtil.ACTIVE_QUEST_PLACEHOLDER.parse(activeQuest, line))
                    .map(line -> PlaceholderUtil.ACTIVE_QUEST_RELATIONAL_PLACEHOLDER.parse(this, activeQuest, line))
                    .collect(Collectors.toList())
            );

            itemStack.setItemMeta(meta);
            return itemStack;
        };

        CompletableFuture<ItemStack> future;

        if(headTexture != null) {
            future =  HeadUtil.createSkull(this.headTexture);
        } else if(headOwner != null) {
            String temp = PlaceholderUtil.PLAYER_PLACEHOLDER.parse(offlinePlayer, headOwner);
            future = HeadUtil.getPlayerHead(temp);
        } else {
            future = CompletableFuture.completedFuture(new ItemStack(this.getMaterial()));
        }

        return future.thenApply(itemStackConsumer);
    }

    public QuestType getType() {
        return this.type;
    }

    public String getCustomType() {
        return this.customType;
    }

    public String getResetId() {
        return this.resetId;
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public int getRequiredAmount() {
        return this.isNumerical() ? getSection().getInt("required_amount") : 1;
    }

    public boolean hasCustomModelData() {
        return this.customModelData != Integer.MIN_VALUE;
    }

    public int getCustomModelData() {
        return this.customModelData;
    }

    @Nullable
    public QuestRequirement<?> getQuestRequirement() {
        return this.questRequirement;
    }

    private void loadConditions(@NotNull ConfigurationSection conditionSection) {
        for(String key : conditionSection.getKeys(false)) {
            final Optional<Condition> optional = PLUGIN.getConditionManager().toInstance(conditionSection, key);
            optional.ifPresent(conditions::add);
        }
    }

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public boolean isTarget(Object object) {
        return object == null ||
                getQuestRequirement() == null
                || getQuestRequirement().isTarget0(object);
    }

    public boolean isNumerical() {
        if(type != QuestType.CUSTOM) {
            return type.isNumerical();
        } else {
            final CustomType customType = PLUGIN.getCustomTypeManager().getCustomType(this.customType);
            return customType != null && customType.isNumerical();
        }
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