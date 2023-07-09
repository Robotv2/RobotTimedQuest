package fr.robotv2.bukkit.ui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import fr.mrmicky.fastinv.InventoryScheme;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ColorUtil;
import fr.robotv2.bukkit.util.PlaceholderUtil;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

        final Material material = Material.matchMaterial(section.getString("material", "BOOK"));
        final String name = section.getString("name");
        final List<String> lore = section.getStringList("lore");

        ItemStack result = new ItemStack(material != null ? material : Material.BOOK);

        final ItemMeta meta = Objects.requireNonNull(result.getItemMeta());

        if(name != null) {
            meta.setDisplayName(ColorUtil.color(name));
        }

        meta.setLore(lore.stream()
                .map(ColorUtil::color)
                .map(line -> PlaceholderUtil.parsePlaceholders(player, line))
                .collect(Collectors.toList()));
        result.setItemMeta(meta);

        final List<String> actions = section.getStringList("on-click");
        return new Pair<>(result, actions.isEmpty()
                ? null : (ignored) -> new StringListProcessor().process(player, actions));
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

                        fastInv.setItem(slot, quest.getGuiItem(activeQuest.getProgress()));

                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                        exception.printStackTrace();
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
