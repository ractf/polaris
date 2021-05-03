package uk.co.ractf.polaris.controller.service;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;

@ExcludeFromGeneratedTestReport
public class ControllerServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        final var serviceBinder = Multibinder.newSetBinder(binder(), Service.class, ControllerServices.class);
        serviceBinder.addBinding().to(TaskScaleReconciliationService.class);
        serviceBinder.addBinding().to(TaskWatchService.class);
    }

}
