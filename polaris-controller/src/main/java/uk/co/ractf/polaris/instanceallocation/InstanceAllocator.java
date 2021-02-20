package uk.co.ractf.polaris.instanceallocation;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;

public interface InstanceAllocator {

    Instance allocate(final InstanceRequest request);

    Instance reset(final InstanceRequest request);

}
