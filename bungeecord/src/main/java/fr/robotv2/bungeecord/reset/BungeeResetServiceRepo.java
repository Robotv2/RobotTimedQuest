package fr.robotv2.bungeecord.reset;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.reset.AbstractResetServiceRepo;

public class BungeeResetServiceRepo extends AbstractResetServiceRepo {

    private final RTQBungeePlugin plugin;

    public BungeeResetServiceRepo(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerServices() {
        registerServices0(plugin.getResetServiceFile());
        startServices(plugin.getBungeeResetPublisher());
    }
}
