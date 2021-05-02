package uk.co.ractf.polaris.controller;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import uk.co.ractf.polaris.api.node.PortAllocations;
import uk.co.ractf.polaris.api.pod.PortMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PortAllocator {

    private final int min;
    private final int max;
    private final PortAllocations portAllocations;

    public PortAllocator(final int min, final int max, final PortAllocations portAllocations) {
        this.min = min;
        this.max = max;
        this.portAllocations = portAllocations;
    }

    private int generatePort(final int min, final int max, final List<Integer> ports) {
        int port;
        do {
            port = ThreadLocalRandom.current().nextInt(min, max);
        } while (ports.contains(port));
        return port;
    }

    public Map<PortMapping, PortBinding> allocate(final List<PortMapping> portMappings) {
        final Map<PortMapping, PortBinding> portBindings = new HashMap<>();
        var lastTcpPort = generatePort(min, max, portAllocations.getTcp());
        var lastUdpPort = portAllocations.getUdp().contains(lastTcpPort) ? generatePort(min, max, portAllocations.getUdp()) : lastTcpPort;

        for (final var portMapping : portMappings) {
            final var tcp = "tcp".equals(portMapping.getProtocol());
            final var port = tcp ? lastTcpPort : lastUdpPort;
            final var protocol = tcp ? InternetProtocol.TCP : InternetProtocol.UDP;
            portBindings.put(portMapping, new PortBinding(new Ports.Binding("0.0.0.0", port + "/" + protocol.toString()),
                    new ExposedPort(portMapping.getPort(), protocol)));

            if (portAllocations.getTcp().contains(lastTcpPort)) {
                lastTcpPort++;
            }
            if (portAllocations.getUdp().contains(lastUdpPort)) {
                lastUdpPort++;
            }
        }
        return portBindings;
    }

}
