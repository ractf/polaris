package uk.co.ractf.polaris.controller;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.instanceallocation.InstanceAllocator;

import java.util.List;
import java.util.Map;

public interface Controller {

    void addHost(final Host host);

    void reconciliationTick();

    Map<String, Challenge> getChallenges();

    Challenge getChallenge(final String id);

    void createChallenge(final Challenge challenge);

    void deleteChallenge(final String id);

    Map<String, Deployment> getDeployments();

    Deployment getDeployment(final String id);

    void createDeployment(final Deployment deployment);

    void updateDeployment(final Deployment deployment);

    void deleteDeployment(final String id);

    Challenge getChallengeFromDeployment(final String deployment);

    Challenge getChallengeFromDeployment(final Deployment deployment);

    Map<String, Host> getHosts();

    Host getHost(final String id);

    List<Deployment> getDeploymentsOfChallenge(final String challenge);

    List<Instance> getInstancesForDeployment(final String deployment);

    InstanceAllocator getInstanceAllocator();

    Instance getInstance(final String id);

}
