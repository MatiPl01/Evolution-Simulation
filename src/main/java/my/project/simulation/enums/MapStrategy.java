package my.project.simulation.enums;

public enum MapStrategy {
    NORMAL,
    MAGIC;

    @Override
    public String toString() {
        return switch (this) {
            case NORMAL -> "normal";
            case MAGIC -> "magic";
        };
    }
}
