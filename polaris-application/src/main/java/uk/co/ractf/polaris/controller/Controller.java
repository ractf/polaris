package uk.co.ractf.polaris.controller;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;

/**
 * The interface that defines what information the Polaris controller needs to receive and how the rest of the application
 * should interact with it.
 */
public interface Controller {

    /**
     * Returns the {@link InstanceAllocator} that should be used to decide which {@link Instance}s are allocated to which users
     *
     * @return the instance allocator
     */
    InstanceAllocator getInstanceAllocator();

}
