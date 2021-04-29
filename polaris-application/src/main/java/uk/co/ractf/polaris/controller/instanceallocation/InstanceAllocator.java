package uk.co.ractf.polaris.controller.instanceallocation;

import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;

/**
 * The interface used to get {@link Instance} allocations for a user/team and request new allocations
 */
public interface InstanceAllocator {

    /**
     * Allocate an {@link Instance} to a user based on the {@link Allocation} rules defined in the {@link Challenge}'s
     * {@link Deployment}, this should return the same instance if called multiple times with the same {@link InstanceRequest},
     * unless a call to {@link #requestNewAllocation(InstanceRequest)} has been made, in which case the instance returned
     * from here will be avoided where possible.
     *
     * @param request the instance request
     * @return the allocated instance
     */
    Instance allocate(final InstanceRequest request);

    /**
     * Discard the current {@link Instance} allocation and get a new one, also add the currently {@link Instance} to an
     * avoid list so the instance allocator can avoid allocating that instance to the same user/team again.
     *
     * @param request the instance request to ge
     * @return a new instance for the user/team
     */
    Instance requestNewAllocation(final InstanceRequest request);

}
