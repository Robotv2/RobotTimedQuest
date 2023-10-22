package fr.robotv2.common.proxy;

import fr.robotv2.common.config.RConfiguration;
import fr.robotv2.common.reset.AbstractResetServiceRepo;

public class ProxyResetServiceRepo extends AbstractResetServiceRepo {

    private final RConfiguration resetServiceConfiguration;
    private final ProxyResetPublisher publisher;

    public ProxyResetServiceRepo(RConfiguration resetServiceConfiguration, ProxyResetPublisher publisher) {
        this.resetServiceConfiguration = resetServiceConfiguration;
        this.publisher = publisher;
    }

    @Override
    public void registerServices() {
        registerServices0(resetServiceConfiguration);
        startServices(publisher);
    }
}
