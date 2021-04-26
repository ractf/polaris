package uk.co.ractf.polaris.host.service;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.management.OperatingSystemMXBean;
import io.dropwizard.util.CharStreams;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.host.Node;
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
public class HostInfoSyncService extends AbstractScheduledService {

    private final Node node;
    private final PolarisConfiguration polarisConfiguration;

    @Inject
    public HostInfoSyncService(final Node node, final PolarisConfiguration polarisConfiguration) {
        this.node = node;
        this.polarisConfiguration = polarisConfiguration;
    }

    private String runCommand(final String command) {
        try {
            final Process process = Runtime.getRuntime().exec(command);
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
            final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            final NodeInfo nodeInfo = new NodeInfo(
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
                    labels);

            node.setHostInfo(nodeInfo);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(polarisConfiguration.getHostInfoSyncFrequency(), polarisConfiguration.getHostInfoSyncFrequency(), TimeUnit.MILLISECONDS);
    }
}
