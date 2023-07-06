package fr.robotv2.bungeecord.reset;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.reset.AbstractResetServiceRepo;
import fr.robotv2.common.reset.ResetService;
import net.md_5.bungee.config.Configuration;

import java.util.TimeZone;

public class BungeeResetServiceRepo extends AbstractResetServiceRepo {

    private final RTQBungeePlugin plugin;

    public BungeeResetServiceRepo(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerServices() {

        stopServices();
        clearServices();

        final Configuration serviceConfiguration = plugin.getResetServiceFile().getConfiguration();

        final TimeZone timeZone;
        final String timeZoneString = serviceConfiguration.getString("options.time-zone", "default");

        if(timeZoneString == null || timeZoneString.equalsIgnoreCase("default")) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = TimeZone.getTimeZone(timeZoneString);
        }

        final Configuration serviceSection = serviceConfiguration.getSection("services");

        if(serviceSection == null) {
            return;
        }

        for(String serviceId : serviceSection.getKeys()) {
            final String cronSyntax = serviceSection.getString(serviceId);
            final ResetService service = new ResetService(serviceId, cronSyntax, timeZone);
            registerService(service);
        }

        startServices(plugin.getBungeeResetPublisher());
    }
}
