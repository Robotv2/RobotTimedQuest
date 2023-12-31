package fr.robotv2.bukkit.listeners.quest;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.cosmetic.BossBarUtil;
import fr.robotv2.bukkit.util.cosmetic.CosmeticUtil;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestBossBarListener implements Listener {

    private final RTQBukkitPlugin plugin;

    public QuestBossBarListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIncrement(QuestIncrementEvent event) {

        final Player player = event.getPlayer();
        final Quest quest = event.getQuest();
        final ActiveQuest activeQuest = event.getActiveQuest();

        final String prefix = "cosmetics.bossbar.";

        if(!plugin.getConfig().getBoolean(prefix.concat("enabled"))) {
            return;
        }

        if(plugin.getCosmeticUtil().isDisabled(player.getUniqueId(), CosmeticUtil.CosmeticType.BOSS_BAR)) {
            return;
        }

        String progressMessage = this.plugin.getConfig().getString(prefix + "progression_message", "&8");

        progressMessage = PlaceholderUtil.QUEST_PLACEHOLDER.parse(quest, progressMessage);
        progressMessage = PlaceholderUtil.ACTIVE_QUEST_PLACEHOLDER.parse(activeQuest, progressMessage);
        progressMessage = PlaceholderUtil.ACTIVE_QUEST_RELATIONAL_PLACEHOLDER.parse(quest, activeQuest, progressMessage);
        progressMessage = ColorUtil.color(progressMessage);

        final BossBar bossBar = BossBarUtil.getOrCreate(player);
        bossBar.setTitle(progressMessage);

        if(!bossBar.isVisible()) {
            bossBar.setVisible(true);
        }

        final int seconds = this.plugin.getConfig().getInt(prefix + "delay", 0);
        final String color = this.plugin.getConfig().getString(prefix + "color", "WHITE");
        final BarColor barColor = Enums.getIfPresent(BarColor.class, color).or(BarColor.WHITE);

        bossBar.setColor(barColor);
        BossBarUtil.showFor(player, seconds);
    }
}
