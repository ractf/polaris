package uk.co.ractf.polaris.node.service;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.management.OperatingSystemMXBean;
import io.dropwizard.util.CharStreams;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.node.PortAllocations;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.node.NodeConfiguration;
import uk.co.ractf.polaris.util.IPChecker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class NodeInfoSyncService extends AbstractScheduledService {

    private final Node node;
    private final NodeConfiguration nodeConfiguration;

    @Inject
    public NodeInfoSyncService(final Node node, final NodeConfiguration nodeConfiguration) {
        this.node = node;
        this.nodeConfiguration = nodeConfiguration;
    }

    private String runCommand(final String command) {
        try {
            final var process = Runtime.getRuntime().exec(command);
            return CharStreams.toString(new InputStreamReader(process.getInputStream(), Charsets.UTF_8));
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to run command", exception);
        }
    }

    @Override
    protected void runOneIteration() {
        try {
            final Map<String, String> labels = new HashMap<>();
            labels.put("aslr", Files.readString(Path.of("/proc/sys/kernel/randomize_va_space")));
            final var operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            final var previousNodeInfo = node.getNodeInfo();
            final var nodeInfo = new NodeInfo(
                    node.getId(),
                    IPChecker.getExternalIP(),
                    InetAddress.getLocalHost().getHostName(),
                    runCommand("uname -a").trim(),
                    operatingSystemMXBean.getArch(),
                    operatingSystemMXBean.getName(),
                    operatingSystemMXBean.getVersion(),
                    operatingSystemMXBean.getAvailableProcessors(),
                    operatingSystemMXBean.getSystemLoadAverage(),
                    operatingSystemMXBean.getTotalPhysicalMemorySize(),
                    operatingSystemMXBean.getFreePhysicalMemorySize(),
                    operatingSystemMXBean.getTotalSwapSpaceSize(),
                    operatingSystemMXBean.getFreeSwapSpaceSize(),
                    labels,
                    previousNodeInfo != null ? previousNodeInfo.getPortAllocations() : PortAllocations.empty(),
                    node.getPodImages(),
                    node.getRunners(),
                    previousNodeInfo == null || previousNodeInfo.isSchedulable());

            node.setNodeInfo(nodeInfo);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(nodeConfiguration.getHostInfoSyncFrequency(), nodeConfiguration.getHostInfoSyncFrequency(), TimeUnit.MILLISECONDS);
    }
}
