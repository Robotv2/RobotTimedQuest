package fr.robotv2.common.reset;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractResetServiceRepo {

    private final Map<String, ResetService> services = new HashMap<>();

    protected void registerService(ResetService service) {
        services.put(service.getId().toLowerCase(), service);
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
        return services.get(id.toLowerCase());
    }

    @UnmodifiableView
    public Collection<ResetService> getServices() {
        return Collections.unmodifiableCollection(services.values());
    }

    @UnmodifiableView
    public Collection<String> getServicesNames() {
        return getServices().stream()
                .map(ResetService::getId)
                .collect(Collectors.toList());
    }

    public boolean serviceExist(String resetId) {
        return getService(resetId) != null;
    }

    public abstract void registerServices();
}
