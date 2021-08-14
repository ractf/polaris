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
            System.out.println(portMapping);
            switch (portMapping.getProtocol()) {
                case "tcp": {
                    portBindings.put(portMapping, new PortBinding(new Ports.Binding("0.0.0.0", lastTcpPort + "/tcp"),
                            new ExposedPort(portMapping.getPort(), InternetProtocol.TCP)));
                    break;
                }
                case "udp": {
                    portBindings.put(portMapping, new PortBinding(new Ports.Binding("0.0.0.0", lastUdpPort + "/udp"),
                            new ExposedPort(portMapping.getPort(), InternetProtocol.UDP)));
                    break;
                }
                case "*": {
                    while (portAllocations.getTcp().contains(lastUdpPort)) {
                        lastUdpPort = generatePort(min, max, portAllocations.getUdp());
                    }
                    portBindings.put(new PortMapping(portMapping.getPort(), "tcp", portMapping.isAdvertise()),
                            new PortBinding(new Ports.Binding("0.0.0.0", lastUdpPort + "/udp"),
                            new ExposedPort(portMapping.getPort(), InternetProtocol.UDP)));
                    portBindings.put(new PortMapping(portMapping.getPort(), "udp", portMapping.isAdvertise()),
                            new PortBinding(new Ports.Binding("0.0.0.0", lastUdpPort + "/tcp"),
                            new ExposedPort(portMapping.getPort(), InternetProtocol.TCP)));
                    break;
                }
            }

            lastTcpPort++;
            lastUdpPort++;
            if (portAllocations.getTcp().contains(lastTcpPort)) {
                lastTcpPort = generatePort(min, max, portAllocations.getTcp());
            }
            if (portAllocations.getUdp().contains(lastUdpPort)) {
                lastUdpPort = portAllocations.getUdp().contains(lastTcpPort) ? generatePort(min, max, portAllocations.getUdp()) : lastTcpPort;
            }
        }
        return portBindings;
    }

}
