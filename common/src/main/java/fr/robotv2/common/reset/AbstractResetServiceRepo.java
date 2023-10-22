package fr.robotv2.common.reset;

import fr.robotv2.common.config.RConfiguration;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractResetServiceRepo {

    private final Map<String, ResetService> services = new HashMap<>();

    protected void registerService(ResetService service) {
        services.put(service.getId().toUpperCase(Locale.ROOT), service);
    }

    protected void clearServices() {
        services.clear();
    }

    protected void startServices(ResetPublisher publisher) {
        getServices().forEach(service -> {
            service.prepareScheduler(publisher);
            service.start();
        });
    }

    protected void stopServices() {
        getServices().forEach(service -> service.getScheduler().stop());
    }

    @Nullable
    public ResetService getService(String id) {
        return services.get(id.toUpperCase(Locale.ROOT));
    }

    @UnmodifiableView
    public Collection<ResetService> getServices() {
        return Collections.unmodifiableCollection(services.values());
    }

    @UnmodifiableView
    public Collection<String> getServicesNames() {
        return getServices().stream()
                .map(ResetService::getId)
                .map(id -> id.toUpperCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    public boolean serviceExist(String resetId) {
        return getService(resetId) != null;
    }

    public abstract void registerServices();

    public void registerServices0(RConfiguration configuration) {

        stopServices();
        clearServices();

        final String timeZoneString = configuration.getString("options.time-zone", "default");
        final TimeZone timeZone = timeZoneString.equalsIgnoreCase("default") ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZoneString);

        for(String serviceId : configuration.getKeys("services")) {
            final String cronSyntax = configuration.getString("services." + serviceId);
            final ResetService service = new ResetService(serviceId, cronSyntax, timeZone);
            this.registerService(service);
        }
    }
}
