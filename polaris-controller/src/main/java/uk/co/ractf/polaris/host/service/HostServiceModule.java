package uk.co.ractf.polaris.host.service;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class HostServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Service> serviceBinder = Multibinder.newSetBinder(binder(), Service.class, HostServices.class);
        serviceBinder.addBinding().to(GarbageCollectionService.class);
        serviceBinder.addBinding().to(HostInfoSyncService.class);
        serviceBinder.addBinding().to(InstanceReconciliationService.class);
        serviceBinder.addBinding().to(OrphanKillerService.class);
    }

}
