package uk.co.ractf.polaris.node.service;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;

@ExcludeFromGeneratedTestReport
public class NodeServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        final var serviceBinder = Multibinder.newSetBinder(binder(), Service.class, NodeServices.class);
        serviceBinder.addBinding().to(GarbageCollectionService.class);
        serviceBinder.addBinding().to(NodeInfoSyncService.class);
        serviceBinder.addBinding().to(InstanceReconciliationService.class);
        serviceBinder.addBinding().to(OrphanKillerService.class);
        serviceBinder.addBinding().to(CredentialRefreshService.class);
        serviceBinder.addBinding().to(AutoUpdateService.class);
    }

}
