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
        final StickyInstances sticky = stickyInstances.computeIfAbsent(request.getChallengeID(), x -> new EphemeralStickyInstances());
        if (sticky.getUser(request.getUserID()) != null) {
            final Instance instance = controller.getInstance(sticky.getUser(request.getUserID()));
            if (instance != null) {
                return instance;
            }
        }
        if (sticky.getTeam(request.getTeamID()) != null) {
            final Instance instance = controller.getInstance(sticky.getTeam(request.getUserID()));
            if (instance != null) {
                return instance;
            }
        }

        final List<Deployment> deployments = controller.getDeploymentsOfChallenge(request.getChallengeID());
        double bestInstanceScore = -1;
        Instance bestInstance = null;
        String bestInstanceSticky = null;
        for (final Deployment deployment : deployments) {
            final Allocation allocation = deployment.getAllocation();
            for (final Instance instance : controller.getInstancesForDeployment(deployment.getID())) {
                if (instanceUsers.get(instance.getID()).size() >= allocation.getUserLimit() ||
                        instanceTeams.get(instance.getID()).size() >= allocation.getTeamLimit() ||
                        userAvoids.get(request.getUserID()).contains(instance.getID()) ||
                        teamAvoids.get(request.getTeamID()).contains(instance.getID())) {
                    continue;
                }
                double userScore = (double) instanceUsers.get(instance.getID()).size() / allocation.getUserLimit();
                double teamScore = (double) instanceTeams.get(instance.getID()).size() / allocation.getTeamLimit();
                double instanceScore = userScore * teamScore;
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
                    if (userAvoids.get(request.getUserID()).contains(instance.getID()) ||
                            teamAvoids.get(request.getTeamID()).contains(instance.getID())) {
                        avoided++;
                    }
                    instanceCount++;
                }
            }
            if (avoided > instanceCount * 0.5) {
                userAvoids.removeAll(request.getUserID());
                teamAvoids.removeAll(request.getTeamID());
            }
            //TODO: notify admins
            Collections.shuffle(deployments);
            List<Instance> instances = controller.getInstancesForDeployment(deployments.get(0).getID());
            Collections.shuffle(instances);
            return instances.get(0);
        }

        if ("user".equals(bestInstanceSticky)) {
            stickyInstances.get(request.getChallengeID()).setUser(bestInstance.getID(), request.getUserID());
        } else if ("team".equals(bestInstanceSticky)) {
            stickyInstances.get(request.getChallengeID()).setTeam(bestInstance.getID(), request.getTeamID());
        }

        return bestInstance;
    }

    @Override
    public Instance requestNewAllocation(final InstanceRequest request) {
        if (stickyInstances.get(request.getChallengeID()).getTeam(request.getTeamID()) != null) {
            teamAvoids.put(request.getTeamID(), stickyInstances.get(request.getChallengeID()).getTeam(request.getTeamID()));
            stickyInstances.get(request.getChallengeID()).clearTeam(request.getTeamID());
        }
        if (stickyInstances.get(request.getChallengeID()).getUser(request.getUserID()) != null) {
            userAvoids.put(request.getUserID(), stickyInstances.get(request.getChallengeID()).getUser(request.getUserID()));
            stickyInstances.get(request.getChallengeID()).clearUser(request.getUserID());
        }
        return allocate(request);
    }

}
