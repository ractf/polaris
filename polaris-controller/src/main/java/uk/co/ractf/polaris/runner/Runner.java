package uk.co.ractf.polaris.runner;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;

public interface Runner<T extends Pod> {

    void startPod(final T pod, final Instance instance);

    void stopPod(final T pod, final Instance instance);

    void forceUpdatePod(final T pod, final Instance instance);

    void restartPod(final T pod, final Instance instance);

    boolean canStartPod(final T pod);

    boolean isPodStarted(final T pod, final Instance instance);

    void preparePod(final T pod);

    void garbageCollect();

    void killOrphans();

    Class<T> getType();

}
