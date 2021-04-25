package uk.co.ractf.polaris.instanceallocation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.controller.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EphemeralInstanceAllocator implements InstanceAllocator {

    private final Controller controller;
    private final Map<String, StickyInstances> stickyInstances = new ConcurrentHashMap<>();
    private final Multimap<String, String> instanceUsers = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> instanceTeams = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> teamAvoids = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> userAvoids = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    public EphemeralInstanceAllocator(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public Instance allocate(final InstanceRequest request) {
        final StickyInstances sticky = stickyInstances.computeIfAbsent(request.getChallenge(), x -> new EphemeralStickyInstances());
        if (sticky.getUser(request.getUser()) != null) {
            final Instance instance = controller.getInstance(sticky.getUser(request.getUser()));
            if (instance != null) {
                return instance;
            }
        }
        if (sticky.getTeam(request.getTeam()) != null) {
            final Instance instance = controller.getInstance(sticky.getTeam(request.getUser()));
            if (instance != null) {
                return instance;
            }
        }

        final List<Deployment> deployments = controller.getDeploymentsOfChallenge(request.getChallenge());
        double bestInstanceScore = -1;
        Instance bestInstance = null;
        String bestInstanceSticky = null;
        for (final Deployment deployment : deployments) {
            final Allocation allocation = deployment.getAllocation();
            for (final Instance instance : controller.getInstancesForDeployment(deployment.getID())) {
                if (instanceUsers.get(instance.getID()).size() >= allocation.getUserLimit() ||
                        instanceTeams.get(instance.getID()).size() >= allocation.getTeamLimit() ||
                        userAvoids.get(request.getUser()).contains(instance.getID()) ||
                        teamAvoids.get(request.getTeam()).contains(instance.getID())) {
                    continue;
                }
                final double userScore = (double) instanceUsers.get(instance.getID()).size() / allocation.getUserLimit();
                final double teamScore = (double) instanceTeams.get(instance.getID()).size() / allocation.getTeamLimit();
                final double instanceScore = userScore * teamScore;
                if (instanceScore > bestInstanceScore) {
                    bestInstanceScore = instanceScore;
                    bestInstance = instance;
                    bestInstanceSticky = allocation.getSticky();
                }
            }
        }

        if (bestInstance == null) {
            int avoided = 0;
            int instanceCount = 0;
            for (final Deployment deployment : deployments) {
                for (final Instance instance : controller.getInstancesForDeployment(deployment.getID())) {
                    if (userAvoids.get(request.getUser()).contains(instance.getID()) ||
                            teamAvoids.get(request.getTeam()).contains(instance.getID())) {
                        avoided++;
                    }
                    instanceCount++;
                }
            }
            if (avoided > instanceCount * 0.5) {
                userAvoids.removeAll(request.getUser());
                teamAvoids.removeAll(request.getTeam());
            }
            //TODO: notify admins
            Collections.shuffle(deployments);
            final List<Instance> instances = controller.getInstancesForDeployment(deployments.get(0).getID());
            Collections.shuffle(instances);
            return instances.get(0);
        }

        if ("user".equals(bestInstanceSticky)) {
            stickyInstances.get(request.getChallenge()).setUser(bestInstance.getID(), request.getUser());
        } else if ("team".equals(bestInstanceSticky)) {
            stickyInstances.get(request.getChallenge()).setTeam(bestInstance.getID(), request.getTeam());
        }

        return bestInstance;
    }

    @Override
    public Instance requestNewAllocation(final InstanceRequest request) {
        if (stickyInstances.get(request.getChallenge()).getTeam(request.getTeam()) != null) {
            teamAvoids.put(request.getTeam(), stickyInstances.get(request.getChallenge()).getTeam(request.getTeam()));
            stickyInstances.get(request.getChallenge()).clearTeam(request.getTeam());
        }
        if (stickyInstances.get(request.getChallenge()).getUser(request.getUser()) != null) {
            userAvoids.put(request.getUser(), stickyInstances.get(request.getChallenge()).getUser(request.getUser()));
            stickyInstances.get(request.getChallenge()).clearUser(request.getUser());
        }
        return allocate(request);
    }

}
