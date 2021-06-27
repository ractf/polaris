package uk.co.ractf.polaris.controller.instanceallocation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EphemeralInstanceAllocator implements InstanceAllocator {

    private final ClusterState clusterState;
    private final Map<NamespacedId, StickyInstances> stickyInstances = new ConcurrentHashMap<>();
    private final Multimap<String, String> instanceUsers = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> instanceTeams = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> teamAvoids = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> userAvoids = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    public EphemeralInstanceAllocator(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Instance allocate(final InstanceRequest request) {
        final var sticky = stickyInstances.computeIfAbsent(request.getTaskId(), x -> new EphemeralStickyInstances());
        final var stickyInstance = getStickyInstance(request, sticky);
        if (stickyInstance != null) {
            return stickyInstance;
        }

        final var challenge = (Challenge) clusterState.getTask(request.getTaskId());
        var instances = new ArrayList<>(clusterState.getInstancesOfTask(request.getTaskId()).values());
        final var possibleInstances = new ArrayList<Instance>();

        //TODO: reimplement allocation logic
        for (final var instance : instances) {
            if (userAvoids.get(request.getUser()).contains(instance.getId()) ||
                    teamAvoids.get(request.getTeam()).contains(instance.getId())) {
                continue;
            }
            possibleInstances.add(instance);
        }
        if (!possibleInstances.isEmpty()) {
            instances = possibleInstances;
        } else {
            userAvoids.removeAll(request.getUser());
            teamAvoids.removeAll(request.getTeam());
        }

        Collections.shuffle(instances);
        final var instance = instances.get(0);

        if ("user".equals(challenge.getAllocation().getSticky())) {
            stickyInstances.get(request.getTaskId()).setUser(instance.getId(), request.getUser());
        } else if ("team".equals(challenge.getAllocation().getSticky())) {
            stickyInstances.get(request.getTaskId()).setTeam(instance.getId(), request.getTeam());
        }

        return instance;
    }

    @Override
    public Instance requestNewAllocation(final InstanceRequest request) {
        if (stickyInstances.get(request.getTaskId()).getTeam(request.getTeam()) != null) {
            teamAvoids.put(request.getTeam(), stickyInstances.get(request.getTaskId()).getTeam(request.getTeam()));
            stickyInstances.get(request.getTaskId()).clearTeam(request.getTeam());
        }
        if (stickyInstances.get(request.getTaskId()).getUser(request.getUser()) != null) {
            userAvoids.put(request.getUser(), stickyInstances.get(request.getTaskId()).getUser(request.getUser()));
            stickyInstances.get(request.getTaskId()).clearUser(request.getUser());
        }
        return allocate(request);
    }

    @Nullable
    private Instance getStickyInstance(final InstanceRequest request, final StickyInstances sticky) {
        if (sticky.getUser(request.getUser()) != null && clusterState.instanceExists(sticky.getUser(request.getUser()))) {
            return clusterState.getInstance(sticky.getUser(request.getUser()));
        }
        if (sticky.getTeam(request.getTeam()) != null && clusterState.instanceExists(sticky.getTeam(request.getUser()))) {
            return clusterState.getInstance(sticky.getTeam(request.getUser()));
        }
        return null;
    }

}
