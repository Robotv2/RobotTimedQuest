package fr.robotv2.bukkit.ui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import fr.mrmicky.fastinv.InventoryScheme;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.QuestInventoryClickEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ItemUtil;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GuiHandler {

    private final RTQBukkitPlugin plugin;

    public GuiHandler(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
        FastInvManager.register(plugin);
    }

    private static final class Pair<A, B> {
        public final A fst;
        public final B snd;

        public Pair(A fst, B snd) {
            this.fst = fst;
            this.snd = snd;
        }
    }

    private Pair<ItemStack, Consumer<InventoryClickEvent>> getItemStackFromCharacter(Player player, Character character) {

        final ConfigurationSection section = plugin.getGuiFile()
                .getConfiguration().getConfigurationSection("quest-gui.items." + String.valueOf(character).toUpperCase());

        if(section == null) {
            return null;
        }

        final ItemStack result = ItemUtil.toItemStack(section, player);
        final List<String> actions = section.getStringList("on_click");

        return new Pair<>(result, actions.isEmpty() ? null : (ignored) -> new StringListProcessor().process(player, actions));
    }

    public FastInv getGuiOf(Player player) {

        final QuestPlayer questPlayer = Objects.requireNonNull(QuestPlayer.getQuestPlayer(player.getUniqueId()));
        final ConfigurationSection section = plugin.getGuiFile().getConfiguration().getConfigurationSection("quest-gui");

        if(section == null) {
            throw new NullPointerException("quest-gui");
        }

        final int row = section.getInt("row", 5);
        final String name = section.getString("name", InventoryType.CHEST.getDefaultTitle());
        final List<String> masks = section.getStringList("pattern");

        final FastInv fastInv = new FastInv(row * 9, ColorUtil.color(name));
        final InventoryScheme scheme = new InventoryScheme();
        masks.forEach(scheme::mask);

        final ConfigurationSection items = section.getConfigurationSection("items");

        if(items != null) {
            for(String item : items.getKeys(false)) {

                final char character = item.toUpperCase().charAt(0);
                final Pair<ItemStack, Consumer<InventoryClickEvent>> pair = this.getItemStackFromCharacter(player, character);

                if(pair == null) {
                    continue;
                }

                scheme.bindItem(character, pair.fst, pair.snd);
            }
        }

        scheme.apply(fastInv);
        final ConfigurationSection resetServices = section.getConfigurationSection("services");

        if(resetServices != null) {
            for(String serviceId : resetServices.getKeys(false)) {
                final ResetService service = plugin.getBukkitResetServiceRepo().getService(serviceId);

                if(service == null) {
                    continue;
                }

                int index = 0;
                final List<ActiveQuest> quests = questPlayer.getActiveQuests(service.getId());

                if(quests.isEmpty()) {
                    continue;
                }

                for(String slotString : resetServices.getStringList(serviceId)) {

                    try {

                        final int slot = Integer.parseInt(slotString);
                        final ActiveQuest activeQuest = quests.get(index);
                        ++index;

                        final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

                        if(quest == null) {
                            continue;
                        }

                        fastInv.setItem(
                                slot,
                                quest.getGuiItem(activeQuest, player),
                                inventoryClickEvent -> {
                                    Bukkit.getPluginManager().callEvent(new QuestInventoryClickEvent(inventoryClickEvent, activeQuest));
                                    GuiHandler.this.plugin.debug("PLAYER CLICKED ON QUEST : " + quest.getId());
                                }
                        );

                    } catch (NumberFormatException exception) {
                        plugin.getLogger().warning(slotString + " is not a valid slot.");
                    } catch (IndexOutOfBoundsException exception) {
                        // there is more slot on the gui.yml than what the player has/is supposed to have
                        // This can easily happen if configuration mismatch.
                        plugin.getLogger().warning("configuration mismatch. There is too many slots for the reset id: " + serviceId + ".");
                    }
                }
            }
        }

        return fastInv;
    }

    public void openMenu(Player player) {
        this.getGuiOf(player).open(player);
    }
}
