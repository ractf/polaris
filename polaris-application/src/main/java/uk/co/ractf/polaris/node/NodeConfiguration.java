package uk.co.ractf.polaris.node;

import uk.co.ractf.polaris.CommonConfiguration;

public class NodeConfiguration extends CommonConfiguration {

    private int hostInfoSyncFrequency;
    private String registryUsername;
    private String registryPassword;
    private boolean killOrphans;
    private String id;

    public int getHostInfoSyncFrequency() {
        return hostInfoSyncFrequency;
    }

    public String getRegistryUsername() {
        return registryUsername;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public boolean isKillOrphans() {
        return killOrphans;
    }

    public String getId() {
        return id;
    }
}
