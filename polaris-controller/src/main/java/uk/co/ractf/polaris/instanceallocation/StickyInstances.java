package uk.co.ractf.polaris.instanceallocation;

interface StickyInstances {

    String getUser(final String user);

    String getTeam(final String team);

    void setUser(final String instance, final String user);

    void setTeam(final String instance, final String team);

    void clearTeam(final String team);

    void clearUser(final String user);

}
