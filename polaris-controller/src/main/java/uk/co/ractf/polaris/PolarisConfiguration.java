package uk.co.ractf.polaris;

import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClientConfiguration;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The config for Polaris
 */
public class PolarisConfiguration extends Configuration {

    private int threadpoolSize;
    private int taskThreadpoolSize;
    private String controllerType;
    private String[] hosts;
    private String apiKey;
    private long reconciliationTickFrequency;
    private String schedulingAlgorithm;
    private int threadpoolTimeoutSeconds;
    private String singleUserUsername;
    private String singleUserPassword;
    private List<String> singleUserRoles;
    private int advertisedMinPort;
    private int advertisedMaxPort;
    private int unadvertisedMinPort;
    private int unadvertisedMaxPort;
    private int hostInfoSyncFrequency;

    @NotNull
    @Valid
    public final ConsulFactory consul = new ConsulFactory();

    @NotNull
    @Valid
    public final RibbonJerseyClientConfiguration client = new RibbonJerseyClientConfiguration();

    public int getThreadpoolSize() {
        return threadpoolSize;
    }

    public void setThreadpoolSize(final int threadpoolSize) {
        this.threadpoolSize = threadpoolSize;
    }

    public int getTaskThreadpoolSize() {
        return taskThreadpoolSize;
    }

    public void setTaskThreadpoolSize(final int taskThreadpoolSize) {
        this.taskThreadpoolSize = taskThreadpoolSize;
    }

    public String getControllerType() {
        return controllerType;
    }

    public void setControllerType(final String controllerType) {
        this.controllerType = controllerType;
    }

    public String[] getHosts() {
        return hosts;
    }

    public void setHosts(String[] hosts) {
        this.hosts = hosts;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public long getReconciliationTickFrequency() {
        return reconciliationTickFrequency;
    }

    public void setReconciliationTickFrequency(long reconciliationTickFrequency) {
        this.reconciliationTickFrequency = reconciliationTickFrequency;
    }

    public String getSchedulingAlgorithm() {
        return schedulingAlgorithm;
    }

    public void setSchedulingAlgorithm(String schedulingAlgorithm) {
        this.schedulingAlgorithm = schedulingAlgorithm;
    }

    public int getThreadpoolTimeoutSeconds() {
        return threadpoolTimeoutSeconds;
    }

    public void setThreadpoolTimeoutSeconds(final int threadpoolTimeoutSeconds) {
        this.threadpoolTimeoutSeconds = threadpoolTimeoutSeconds;
    }

    public String getSingleUserUsername() {
        return singleUserUsername;
    }

    public void setSingleUserUsername(final String singleUserUsername) {
        this.singleUserUsername = singleUserUsername;
    }

    public String getSingleUserPassword() {
        return singleUserPassword;
    }

    public void setSingleUserPassword(final String singleUserPassword) {
        this.singleUserPassword = singleUserPassword;
    }

    public List<String> getSingleUserRoles() {
        return singleUserRoles;
    }

    public void setSingleUserRoles(final List<String> singleUserRoles) {
        this.singleUserRoles = singleUserRoles;
    }

    public ConsulFactory getConsulFactory() {
        return consul;
    }

    public RibbonJerseyClientConfiguration getClient() {
        return client;
    }

    public int getAdvertisedMinPort() {
        return advertisedMinPort;
    }

    public void setAdvertisedMinPort(final int advertisedMinPort) {
        this.advertisedMinPort = advertisedMinPort;
    }

    public int getAdvertisedMaxPort() {
        return advertisedMaxPort;
    }

    public void setAdvertisedMaxPort(final int advertisedMaxPort) {
        this.advertisedMaxPort = advertisedMaxPort;
    }

    public int getUnadvertisedMinPort() {
        return unadvertisedMinPort;
    }

    public void setUnadvertisedMinPort(final int unadvertisedMinPort) {
        this.unadvertisedMinPort = unadvertisedMinPort;
    }

    public int getUnadvertisedMaxPort() {
        return unadvertisedMaxPort;
    }

    public void setUnadvertisedMaxPort(final int unadvertisedMaxPort) {
        this.unadvertisedMaxPort = unadvertisedMaxPort;
    }

    public int getHostInfoSyncFrequency() {
        return hostInfoSyncFrequency;
    }

    public void setHostInfoSyncFrequency(final int hostInfoSyncFrequency) {
        this.hostInfoSyncFrequency = hostInfoSyncFrequency;
    }
}
