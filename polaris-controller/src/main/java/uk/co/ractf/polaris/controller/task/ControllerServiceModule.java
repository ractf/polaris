package uk.co.ractf.polaris.controller.task;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import uk.co.ractf.polaris.annotation.ExcludeFromGeneratedReport;

@ExcludeFromGeneratedReport
public class ControllerServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        final Multibinder<Service> serviceBinder = Multibinder.newSetBinder(binder(), Service.class, ControllerServices.class);
        serviceBinder.addBinding().to(DeploymentScaleReconciliationService.class);
    }

}
