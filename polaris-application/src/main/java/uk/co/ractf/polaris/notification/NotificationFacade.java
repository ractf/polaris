package uk.co.ractf.polaris.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.notification.Notification;
import uk.co.ractf.polaris.api.notification.NotificationLevel;
import uk.co.ractf.polaris.api.notification.NotificationTarget;
import uk.co.ractf.polaris.state.ClusterState;

@Singleton
public class NotificationFacade {

    private final ClusterState state;

    @Inject
    public NotificationFacade(final ClusterState state) {
        this.state = state;
    }

    public void notify(final Notification notification) {
        if (notification.getTarget() == NotificationTarget.SYSTEM_ADMIN) {
            for (final var receiver : state.getNotificationReceivers("polaris").values()) {
                if (notification.getLevel().asInt() >= receiver.getMinimumLevel().asInt() &&
                        receiver.isSystem()) {
                    Notifier.from(receiver).notify(notification);
                }
            }
        } else if (notification.getTarget() == NotificationTarget.NAMESPACE_ADMIN) {
            for (final var receiver : state.getNotificationReceivers(notification.getNamespace().getName()).values()) {
                if (notification.getLevel().asInt() >= receiver.getMinimumLevel().asInt()) {
                    Notifier.from(receiver).notify(notification);
                }
            }
        }
    }

    public void error(final String summary, final String detail) {
        notify(new Notification(NotificationLevel.ERROR, NotificationTarget.SYSTEM_ADMIN, null, summary, detail));
    }

    public void error(final NotificationTarget target, final Namespace namespace,
                      final String summary, final String detail) {
        notify(new Notification(NotificationLevel.ERROR, target, namespace, summary, detail));
    }

    public void warning(final String summary, final String detail) {
        notify(new Notification(NotificationLevel.WARNING, NotificationTarget.SYSTEM_ADMIN, null, summary, detail));
    }

    public void warning(final NotificationTarget target, final Namespace namespace,
                      final String summary, final String detail) {
        notify(new Notification(NotificationLevel.WARNING, target, namespace, summary, detail));
    }

    public void info(final String summary, final String detail) {
        notify(new Notification(NotificationLevel.INFO, NotificationTarget.SYSTEM_ADMIN, null, summary, detail));
    }

    public void info(final NotificationTarget target, final Namespace namespace,
                      final String summary, final String detail) {
        notify(new Notification(NotificationLevel.INFO, target, namespace, summary, detail));
    }

    public void debug(final String summary, final String detail) {
        notify(new Notification(NotificationLevel.DEBUG, NotificationTarget.SYSTEM_ADMIN, null, summary, detail));
    }

    public void debug(final NotificationTarget target, final Namespace namespace,
                      final String summary, final String detail) {
        notify(new Notification(NotificationLevel.DEBUG, target, namespace, summary, detail));
    }

}
