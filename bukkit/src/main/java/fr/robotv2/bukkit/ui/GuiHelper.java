package fr.robotv2.bukkit.ui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.InventoryScheme;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.QuestInventoryClickEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.item.ItemUtil;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GuiHelper {

    private final RTQBukkitPlugin plugin;

    public GuiHelper(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    private void runTaskOnBukkitThread(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    private CompletableFuture<Void> allOf(Collection<CompletableFuture<Void>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> handleItems(ConfigurationSection section, InventoryScheme scheme, FastInv fastInv, Player player) {

        final List<CompletableFuture<Void>> futuresItems = new ArrayList<>();
        final ConfigurationSection items = section.getConfigurationSection("items");

        if(items == null) {
            return CompletableFuture.completedFuture(null);
        }

        for (String item : items.getKeys(false)) {

            final char character = item.toUpperCase().charAt(0);
            final ConfigurationSection itemSection = plugin.getGuiFile().getConfiguration().getConfigurationSection("quest-gui.items." + String.valueOf(character).toUpperCase());

            if (itemSection == null) {
                continue;
            }

            final List<String> actions = section.getStringList("on_click");
            final CompletableFuture<Void> future = ItemUtil.toItemStack(itemSection)
                    .thenAccept(completedItem -> scheme.bindItem(character, completedItem, actions.isEmpty() ? null : ignored -> new StringListProcessor().process(player, actions)));
            futuresItems.add(future);
        }

        return allOf(futuresItems).thenAccept(ignored -> runTaskOnBukkitThread(() -> scheme.apply(fastInv)));
    }

    public CompletableFuture<Void> handleServices(ConfigurationSection section, FastInv fastInv, Player player) {

        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        final ConfigurationSection resetServices = section.getConfigurationSection("services");
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId());

        if(resetServices == null || questPlayer == null) {
            return CompletableFuture.completedFuture(null);
        }

        for (String serviceId : resetServices.getKeys(false)) {

            final ResetService service = plugin.getBukkitResetServiceRepo().getService(serviceId);

            if (service == null) {
                continue;
            }

            int index = 0;
            final List<ActiveQuest> quests = questPlayer.getActiveQuests(service.getId());

            if (quests.isEmpty()) {
                continue;
            }

            for (String slotString : resetServices.getStringList(serviceId)) {

                try {

                    final int slot = Integer.parseInt(slotString);
                    final ActiveQuest activeQuest = quests.get(index);
                    ++index;

                    final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

                    if (quest == null) {
                        continue;
                    }

                    final CompletableFuture<Void> future = quest.getGuiItem(activeQuest, player).thenAccept(itemStack -> {
                        runTaskOnBukkitThread(() -> fastInv.setItem(
                                slot,
                                itemStack,
                                inventoryClickEvent -> Bukkit.getPluginManager().callEvent(new QuestInventoryClickEvent(inventoryClickEvent, activeQuest))
                        ));
                    });

                    futures.add(future);

                } catch (NumberFormatException exception) {
                    plugin.getLogger().warning(slotString + " is not a valid slot.");
                } catch (IndexOutOfBoundsException exception) {
                    // there is more slot on the gui.yml than what the player has/is supposed to have
                    // This can easily happen if configuration mismatch.
                    plugin.getLogger().warning("configuration mismatch. There is too many slots for the reset id: " + serviceId + ".");
                }
            }
        }

        return allOf(futures);
    }


}
