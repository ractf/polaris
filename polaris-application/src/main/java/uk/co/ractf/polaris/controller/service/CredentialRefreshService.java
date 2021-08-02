package uk.co.ractf.polaris.controller.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ecr.AmazonECRClientBuilder;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.util.Base64;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.notification.NotificationTarget;
import uk.co.ractf.polaris.api.registry.credentials.AWSCredentials;
import uk.co.ractf.polaris.notification.NotificationFacade;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.concurrent.TimeUnit;

@Singleton
public class CredentialRefreshService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(CredentialRefreshService.class);

    private final ClusterState clusterState;
    private final NotificationFacade notifications;

    @Inject
    public CredentialRefreshService(final ClusterState clusterState, final NotificationFacade notifications) {
        this.clusterState = clusterState;
        this.notifications = notifications;
    }

    @Override
    public void runOneIteration() {
        for (final var entry : clusterState.getCredentials().entrySet()) {
            if (entry.getValue() instanceof AWSCredentials) {
                try {
                    final var awsKeys = (AWSCredentials) entry.getValue();
                    final var credentials = new BasicAWSCredentials(awsKeys.getAccessKey(), awsKeys.getSecretKey());
                    final var ecr = AmazonECRClientBuilder
                            .standard()
                            .withRegion(awsKeys.getRegion())
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .build();
                    final var auth = ecr.getAuthorizationToken(new GetAuthorizationTokenRequest());

                    final var newCreds = new AWSCredentials(awsKeys.getId(), "aws", awsKeys.getAccessKey(),
                            awsKeys.getSecretKey(), awsKeys.getRegion(),
                            new String(Base64.decode(auth.getAuthorizationData().get(0).getAuthorizationToken())));

                    clusterState.setCredential(newCreds);
                } catch (final Exception e) {
                    log.error("Failed to get AWS credentials", e);
                    notifications.warning(NotificationTarget.NAMESPACE_ADMIN, clusterState.getNamespace(entry.getValue().getId().getNamespace()),
                            "Could not update AWS credentials.", e.getMessage());
                }
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(20, 20, TimeUnit.SECONDS);
    }
}
