package uk.co.ractf.polaris.controller;

import uk.co.ractf.polaris.api.instance.Instance;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class SemaphoreInstanceList {

    private final List<Instance> instances = new CopyOnWriteArrayList<>();
    private final Semaphore semaphore = new Semaphore(1);

    public void free() {
        semaphore.release();
    }

    public List<Instance> getInstances() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return instances;
    }

    public List<Instance> getReadOnlyInstances() {
        return Collections.unmodifiableList(instances);
    }

    public boolean isBusy() {
        return semaphore.availablePermits() < 1;
    }

}
