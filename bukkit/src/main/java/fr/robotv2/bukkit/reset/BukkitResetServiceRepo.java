package fr.robotv2.bukkit.reset;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.reset.AbstractResetServiceRepo;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.TimeZone;

public class BukkitResetServiceRepo extends AbstractResetServiceRepo {

    private final RTQBukkitPlugin plugin;

    public BukkitResetServiceRepo(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerServices() {

        stopServices();
        clearServices();

        final YamlConfiguration serviceConfiguration = plugin.getResetServiceFile().getConfiguration();

        final TimeZone timeZone;
        final String timeZoneString = serviceConfiguration.getString("options.time-zone", "default");

        if(timeZoneString == null || timeZoneString.equalsIgnoreCase("default")) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = TimeZone.getTimeZone(timeZoneString);
        }

        final ConfigurationSection serviceSection = serviceConfiguration.getConfigurationSection("services");

        if(serviceSection == null) {
            return;
        }

        for(String serviceId : serviceSection.getKeys(false)) {
            final String cronSyntax = serviceSection.getString(serviceId);
            final ResetService service = new ResetService(serviceId, cronSyntax, timeZone);
            this.registerService(service);
        }

        startServices(plugin.getResetPublisher());
    }
}
