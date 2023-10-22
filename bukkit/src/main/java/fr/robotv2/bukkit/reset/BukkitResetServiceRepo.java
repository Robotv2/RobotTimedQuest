package fr.robotv2.bukkit.reset;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.reset.AbstractResetServiceRepo;

public class BukkitResetServiceRepo extends AbstractResetServiceRepo {

    private final RTQBukkitPlugin plugin;

    public BukkitResetServiceRepo(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerServices() {

        super.registerServices0(plugin.getResetServiceFile());

        if(!plugin.isBungeecordMode()) {
            startServices(plugin.getResetPublisher());
        }
    }
}
