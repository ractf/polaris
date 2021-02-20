package uk.co.ractf.polaris.instanceallocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EphemeralStickyInstances implements StickyInstances {

    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<String, String> teams = new ConcurrentHashMap<>();

    @Override
    public String getUser(final String user) {
        return users.get(user);
    }

    @Override
    public String getTeam(final String team) {
        return teams.get(team);
    }

    @Override
    public void setUser(final String instance, final String user) {
        users.put(user, instance);
    }

    @Override
    public void setTeam(final String instance, final String team) {
        teams.put(team, instance);
    }

    @Override
    public void clearTeam(final String team) {
        teams.remove(team);
    }

    @Override
    public void clearUser(final String user) {
        users.remove(user);
    }

}
