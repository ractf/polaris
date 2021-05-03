package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;

/**
 * The default implementation of {@link SchedulingAlgorithm}. The scheduler places {@link Task}s onto nodes by running a series of
 * {@link Plugin}s to filter and score the nodes to find the best place to run instances of the task.
 * <p>
 * This is done in the following stages:
 * - Checking the cluster meets the conditions required to run the task
 * - Filtering which nodes can run the task
 * - Scoring the nodes
 * <p>
 * In the future, this might be extended to include things such as evicting lower priority tasks.
 */
@Singleton
public class GenericSchedulingAlgorithm implements SchedulingAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(GenericSchedulingAlgorithm.class);

    private final ClusterState clusterState;
    private final List<ClusterPredicatePlugin> clusterPredicatePlugins;
    private final List<FilterPlugin> filterPlugins;
    private final List<ScorePlugin> scorePlugins;

    @Inject
    public GenericSchedulingAlgorithm(final ClusterState clusterState, final List<ClusterPredicatePlugin> clusterPredicatePlugins,
                                      final List<FilterPlugin> filterPlugins, final List<ScorePlugin> scorePlugins) {
        this.clusterState = clusterState;
        this.clusterPredicatePlugins = clusterPredicatePlugins;
        this.filterPlugins = filterPlugins;
        this.scorePlugins = scorePlugins;
    }

    @Override
    public ScheduleResult schedule(final Task task) {
        final var result = runPredicates(task);
        if (result != null) {
            return result;
        }

        final List<NodeInfo> possibleNodes = new ArrayList<>();
        final List<NodeInfo> resolvableNodes = new ArrayList<>();
        filterNodes(task, possibleNodes, resolvableNodes);

        final var pluginScores = scoreNodes(task, possibleNodes);

        normaliseScores(possibleNodes, pluginScores);

        final var node = getHighestScoringNode(possibleNodes, pluginScores);

        return new ScheduleResult(node, true, possibleNodes.size() + resolvableNodes.size(),
                possibleNodes.size(), Collections.emptyList());
    }

    private NodeInfo getHighestScoringNode(final List<NodeInfo> possibleNodes, final Map<NodeInfo, Map<ScorePlugin, Double>> pluginScores) {
        final Map<NodeInfo, Double> scores = new HashMap<>();
        for (final var node : possibleNodes) {
            final var total = pluginScores.get(node).values().stream().mapToDouble(Double::doubleValue).sum();
            scores.put(node, total);
        }

        return Collections.max(scores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
    }

    private void normaliseScores(final List<NodeInfo> possibleNodes, final Map<NodeInfo, Map<ScorePlugin, Double>> pluginScores) {
        for (final var plugin : scorePlugins) {
            var min = Double.MAX_VALUE;
            var max = Double.MIN_VALUE;
            for (final var node : possibleNodes) {
                min = Math.min(pluginScores.get(node).get(plugin), min);
                max = Math.max(pluginScores.get(node).get(plugin), max);
            }
            for (final var node : possibleNodes) {
                final var currentScore = pluginScores.get(node).get(plugin);
                final var normalisedScore = (currentScore - min) / (max - min);
                pluginScores.get(node).put(plugin, normalisedScore * plugin.getWeight());
            }
        }
    }

    @NotNull
    private Map<NodeInfo, Map<ScorePlugin, Double>> scoreNodes(final Task task, final List<NodeInfo> possibleNodes) {
        final Map<NodeInfo, Map<ScorePlugin, Double>> pluginScores = new HashMap<>();
        for (final var node : possibleNodes) {
            pluginScores.put(node, new HashMap<>());
            for (final var plugin : scorePlugins) {
                pluginScores.get(node).put(plugin, plugin.score(task, node));
            }
        }
        return pluginScores;
    }

    private void filterNodes(final Task task, final List<NodeInfo> possibleNodes, final List<NodeInfo> resolvableNodes) {
        for (final var entry : clusterState.getNodes().entrySet()) {
            var schedulable = true;
            var resolvable = true;
            for (final var plugin : filterPlugins) {
                final var filterResult = plugin.filter(task, entry.getValue());
                schedulable = schedulable && filterResult.isSchedulable();
                if (!filterResult.isSchedulable()) {
                    resolvable = resolvable && filterResult.isResolvable();
                }
            }
            if (schedulable) {
                possibleNodes.add(entry.getValue());
            } else if (resolvable) {
                resolvableNodes.add(entry.getValue());
            }
        }
    }

    @Nullable
    private ScheduleResult runPredicates(final Task task) {
        for (final var plugin : clusterPredicatePlugins) {
            final var result = plugin.canSchedule(task);
            if (!result.isPossible()) {
                log.debug("Failed to schedule task {} because {}", task.getId(), result.getReason());
                return new ScheduleResult(null, false, 0, 0, result.getReason());
            }
        }
        return null;
    }
}
