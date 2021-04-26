package uk.co.ractf.polaris;

import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClientConfiguration;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

//TODO: Remove everything from here that isn't required to boot, move it into consul
public class CommonConfiguration extends Configuration {

    @NotNull
    @Valid
    public final ConsulFactory consul = new ConsulFactory();
    @NotNull
    @Valid
    public final RibbonJerseyClientConfiguration client = new RibbonJerseyClientConfiguration();
    private int threadpoolSize;
    private int taskThreadpoolSize;
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

    public ConsulFactory getConsulFactory() {
        return consul;
    }

    public RibbonJerseyClientConfiguration getClient() {
        return client;
    }

    public int getThreadpoolSize() {
        return threadpoolSize;
    }

    public int getTaskThreadpoolSize() {
        return taskThreadpoolSize;
    }

    public long getReconciliationTickFrequency() {
        return reconciliationTickFrequency;
    }

    public String getSchedulingAlgorithm() {
        return schedulingAlgorithm;
    }

    public int getThreadpoolTimeoutSeconds() {
        return threadpoolTimeoutSeconds;
    }

    public String getSingleUserUsername() {
        return singleUserUsername;
    }

    public String getSingleUserPassword() {
        return singleUserPassword;
    }

    public List<String> getSingleUserRoles() {
        return singleUserRoles;
    }

    public int getAdvertisedMinPort() {
        return advertisedMinPort;
    }

    public int getAdvertisedMaxPort() {
        return advertisedMaxPort;
    }

    public int getUnadvertisedMinPort() {
        return unadvertisedMinPort;
    }

    public int getUnadvertisedMaxPort() {
        return unadvertisedMaxPort;
    }

}
