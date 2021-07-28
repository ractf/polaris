package uk.co.ractf.polaris.api.notification;

public enum NotificationLevel {
    ERROR(30, "#FF0000", "ERROR"),
    WARNING(20, "#FFFF00", "WARNING"),
    INFO(10, "#00FF00", "INFO"),
    DEBUG(0, "#0000FF", "DEBUG");

    private final int level;
    private final String colour;
    private final String name;

    NotificationLevel(final int level, final String colour, final String name) {
        this.level = level;
        this.colour = colour;
        this.name = name;
    }

    public int asInt() {
        return level;
    }

    public String getColour() {
        return colour;
    }

    public String getName() {
        return name;
    }
}
