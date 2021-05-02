package uk.co.ractf.polaris.controller.service;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.orbitz.consul.Consul;
import com.orbitz.consul.cache.KVCache;
import uk.co.ractf.polaris.state.ClusterState;
import uk.co.ractf.polaris.util.ConsulPath;

public class TaskWatchService extends AbstractIdleService {

    private final Consul consul;
    private final ClusterState clusterState;
    private final KVCache cache;

    @Inject
    public TaskWatchService(final Consul consul, final ClusterState clusterState) {
        this.consul = consul;
        this.clusterState = clusterState;
        cache = KVCache.newCache(consul.keyValueClient(), ConsulPath.tasks());
    }

    @Override
    protected void startUp() throws Exception {
        //this triggers every time the thing changes, with the new set of values
        cache.addListener(newValues -> {

        });
        cache.start();
    }

    @Override
    protected void shutDown() throws Exception {
        cache.stop();
    }
}
