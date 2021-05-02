package uk.co.ractf.polaris.api.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the current state of a Polaris host
 *
 * <pre>
 *       {
 *     "id": "embedded",
 *     "hostname": "polaris1",
 *     "uname": "Linux polaris1 5.10.16-arch1-1 #1 SMP PREEMPT Sat, 13 Feb 2021 20:50:18 +0000 x86_64 GNU/Linux",
 *     "architecture": "amd64",
 *     "processors": 16,
 *     "totalMemory": 67437092864,
 *     "freeMemory": 38039498752,
 *     "totalSwap": 24771100672,
 *     "freeSwap": 24771100672,
 *     "labels": {
 *         "aslr": "on"
 *     },
 *     "publicIP": "127.0.0.1",
 *     "osName": "Linux",
 *     "osVersion": "5.10.16-arch1-1",
 *     "cpuLoad": 6.22
 *   }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeInfo extends JsonRepresentable {

    private final String id;
    private final String publicIP;
    private final String hostname;
    private final String uname;
    private final String architecture;
    private final String osName;
    private final String osVersion;
    private final int processors;
    private final double cpuLoad;
    private final long totalMemory;
    private final long freeMemory;
    private final long totalSwap;
    private final long freeSwap;
    private final Map<String, String> labels;
    private final PortAllocations portAllocations;
    private final List<String> podImages;
    private final List<String> runners;
    private final boolean schedulable;

    /**
     * @param id              the id of the host
     * @param publicIP        the public ip of the host
     * @param hostname        the hostname of the host
     * @param uname           the kernel details of the host
     * @param architecture    the architecture of the host
     * @param osName          the name of the operating system
     * @param osVersion       the version of the operating system
     * @param processors      how many processors are available
     * @param cpuLoad         the average cpu load across all cores in the last minute
     * @param totalMemory     total system memory
     * @param freeMemory      free system memory
     * @param totalSwap       total swap space on the host
     * @param freeSwap        free swap space on the host
     * @param labels          labels added to the host
     * @param portAllocations ports allocated on the host
     * @param podImages       pod images downloaded on the node
     * @param runners         the runners available on the node
     * @param schedulable     if the node is schedulable
     */
    public NodeInfo(
            @JsonProperty("id") final String id,
            @JsonProperty("publicip") final String publicIP,
            @JsonProperty("hostname") final String hostname,
            @JsonProperty("uname") final String uname,
            @JsonProperty("architecture") final String architecture,
            @JsonProperty("osname") final String osName,
            @JsonProperty("osversion") final String osVersion,
            @JsonProperty("processors") final int processors,
            @JsonProperty("cpuload") final double cpuLoad,
            @JsonProperty("totalMemory") final long totalMemory,
            @JsonProperty("freeMemory") final long freeMemory,
            @JsonProperty("totalSwap") final long totalSwap,
            @JsonProperty("freeSwap") final long freeSwap,
            @JsonProperty("labels") final Map<String, String> labels,
            @JsonProperty("portAllocations") final PortAllocations portAllocations,
            @JsonProperty("podImages") final List<String> podImages,
            @JsonProperty("runners") final List<String> runners,
            @JsonProperty("schedulable") final boolean schedulable) {
        this.id = id;
        this.publicIP = publicIP;
        this.hostname = hostname;
        this.uname = uname;
        this.architecture = architecture;
        this.osName = osName;
        this.osVersion = osVersion;
        this.processors = processors;
        this.cpuLoad = cpuLoad;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
        this.totalSwap = totalSwap;
        this.freeSwap = freeSwap;
        this.labels = labels;
        this.portAllocations = portAllocations;
        this.podImages = podImages;
        this.runners = runners;
        this.schedulable = schedulable;
    }

    public String getId() {
        return id;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUname() {
        return uname;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public int getProcessors() {
        return processors;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalSwap() {
        return totalSwap;
    }

    public long getFreeSwap() {
        return freeSwap;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public PortAllocations getPortAllocations() {
        return portAllocations;
    }

    public List<String> getPodImages() {
        return podImages;
    }

    public List<String> getRunners() {
        return runners;
    }

    public boolean isSchedulable() {
        return schedulable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        final NodeInfo nodeInfo = (NodeInfo) o;
        return processors == nodeInfo.processors && Double.compare(nodeInfo.cpuLoad, cpuLoad) == 0 &&
                totalMemory == nodeInfo.totalMemory && freeMemory == nodeInfo.freeMemory &&
                totalSwap == nodeInfo.totalSwap && freeSwap == nodeInfo.freeSwap &&
                schedulable == nodeInfo.schedulable && Objects.equals(id, nodeInfo.id) &&
                Objects.equals(publicIP, nodeInfo.publicIP) && Objects.equals(hostname, nodeInfo.hostname) &&
                Objects.equals(uname, nodeInfo.uname) && Objects.equals(architecture, nodeInfo.architecture) &&
                Objects.equals(osName, nodeInfo.osName) && Objects.equals(osVersion, nodeInfo.osVersion) &&
                Objects.equals(labels, nodeInfo.labels) && Objects.equals(portAllocations, nodeInfo.portAllocations) &&
                Objects.equals(podImages, nodeInfo.podImages) && Objects.equals(runners, nodeInfo.runners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicIP, hostname, uname, architecture, osName, osVersion, processors, cpuLoad,
                totalMemory, freeMemory, totalSwap, freeSwap, labels, portAllocations, podImages, runners, schedulable);
    }
}
