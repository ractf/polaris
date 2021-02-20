package uk.co.ractf.polaris;

import io.dropwizard.Configuration;

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

}
