package fr.robotv2.bukkit.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {

    private static final SplittableRandom RANDOM = new SplittableRandom();

    private final RTQBukkitPlugin plugin;
    private final Map<String, Quest> quests = new HashMap<>();

    public QuestManager(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public void clearQuests() {
        this.quests.clear();
    }

    private void cacheQuest(@NotNull Quest quest) {
        this.quests.put(quest.getId(), quest);
    }

    @UnmodifiableView
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(this.quests.values());
    }

    @UnmodifiableView
    public List<Quest> getQuests(String resetId) {
        return getQuests().stream()
                .filter(quest -> quest.getResetId().equals(resetId))
                .collect(Collectors.toList());
    }

    @Nullable
    public Quest getRandomQuest(String resetId) {
        final List<Quest> quests = this.getQuests(resetId);

        if(quests.isEmpty()) {
            return null; // no quest for this reset id.
        }

        return quests.get(RANDOM.nextInt(quests.size()));
    }

    /**
     * Fill the player quest with all the missing quest in the reset service
     * @return the number of quest added
     */
    public int fillPlayer(QuestPlayer questPlayer) {
        int total = 0;
        for(ResetService service : plugin.getBukkitResetServiceRepo().getServices()) {
            total += fillPlayer(questPlayer, service.getId());
        }
        return total;
    }

    /**
     * Fill the player quest with all the missing quest in the reset service
     * @return the number of quest added
     */
    public int fillPlayer(QuestPlayer questPlayer, String resetId) {
        final int required = plugin.getConfig().getInt("quest-assignment." + resetId, 0);
        if(required != 0) {

            final long playerNumber = questPlayer.getActiveQuests().stream()
                    .filter(quest -> quest.getResetId().equalsIgnoreCase(resetId))
                    .count();

            if(required > playerNumber) {
                final int diff = (int) (required - playerNumber);
                return this.fillPlayer(questPlayer, resetId, diff);
            } else if(required != playerNumber) {
                final Player player = Bukkit.getPlayer(questPlayer.getUniqueId());
                plugin.getLogger().warning(String.format("The player %s seem to have an unusual number of quest for reset id %s", player != null ? player.getName() : "UNKNOWN", resetId));
            }
        }

        return 0;
    }

    public int fillPlayer(QuestPlayer questPlayer, String resetId, int amount) {

        final List<Quest> quests = new ArrayList<>();
        final int max = this.getQuests(resetId).size();

        while(quests.size() < amount) {

            if(quests.size() >= max) {
                break;
            }

            final Quest random = this.getRandomQuest(resetId);

            if(random == null) {
                break; //There is no quest available for this delay.
            }

            if(!plugin.getBukkitResetServiceRepo().serviceExist(random.getResetId())) {
                plugin.getLogger().warning(String.format("The quest '%s' seem to have an invalid reset_id: %s", random.getId(), random.getResetId()));
                continue;
            }

            if(!quests.contains(random)) {
                quests.add(random);
            }
        }

        for(Quest quest : quests) {
            final ResetService service = this.plugin.getBukkitResetServiceRepo().getService(quest.getResetId());
            final ActiveQuest activeQuest = new ActiveQuest(questPlayer.getUniqueId(), quest.getId(), service);
            questPlayer.addActiveQuest(activeQuest);
        }

        return quests.size();
    }

    public void loadQuests(@NotNull String resourcePath) {
        final File file = new File(plugin.getDataFolder(), resourcePath);

        if(!file.exists()) {
            plugin.getLogger().warning(resourcePath + " is marked as resource path but doesn't exist.");
            return;
        }

        this.loadQuests(YamlConfiguration.loadConfiguration(file));
    }

    public void loadQuests(@NotNull FileConfiguration configuration) {

        final ConfigurationSection section = configuration.getConfigurationSection("quests");

        if(section == null) {
            this.plugin.getLogger().warning("the file " + configuration.getName() + " ");
            return;
        }

        for(String key : section.getKeys(false)) {

            final ConfigurationSection questSection = section.getConfigurationSection(key);

            if(questSection == null) {
                continue;
            }

            try {
                final Quest quest = new Quest(questSection);
                this.cacheQuest(quest);
                this.plugin.getLogger().info(key + " has been loaded successfully.");
            } catch (Exception exception) {
                plugin.getLogger().warning(" ");
                plugin.getLogger().warning(" WARNING - " + key);
                plugin.getLogger().warning("An error occurred while loading quest '" + key);
                plugin.getLogger().warning("Error's message: " + exception.getMessage());
                plugin.getLogger().warning(" ");
                plugin.getLogger().warning("This quest will not be loaded. Please fix it and then reload");
                plugin.getLogger().warning("the plugin's configuration files.");
                plugin.getLogger().warning(" ");
            }
        }
    }

    public Quest fromId(@NotNull String id) {
        return quests.get(id);
    }
}
