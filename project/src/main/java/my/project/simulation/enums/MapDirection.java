package my.project.simulation.enums;

import my.project.simulation.utils.Vector2D;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString() {
        return switch(this) {
            case NORTH     -> "North";
            case NORTHEAST -> "North East";
            case EAST      -> "East";
            case SOUTHEAST -> "South East";
            case SOUTH     -> "South";
            case SOUTHWEST -> "South West";
            case WEST      -> "West";
            case NORTHWEST -> "North West";
        };
    }

    public MapDirection rotate(int angleNum) {
        MapDirection[] values = MapDirection.values();
        return values[(this.ordinal() + angleNum) % values.length];
    }

    public int getAngle() {
        return this.ordinal() * 45;
    }

    public Vector2D toUnitVector() {
        return switch (this) {
            case NORTH     -> new Vector2D(0, 1);
            case NORTHEAST -> new Vector2D(1, 1);
            case EAST      -> new Vector2D(1, 0);
            case SOUTHEAST -> new Vector2D(1, -1);
            case SOUTH     -> new Vector2D(0, -1);
            case SOUTHWEST -> new Vector2D(-1, -1);
            case WEST      -> new Vector2D(-1, 0);
            case NORTHWEST -> new Vector2D(-1, 1);
        };
    }
}
