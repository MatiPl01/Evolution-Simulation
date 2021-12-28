package my.project.simulation.maps;

public enum MapType {
    FOLDING,
    FENCED;

    @Override
    public String toString() {
        return switch (this) {
            case FOLDING -> "Folding map";
            case FENCED -> "Fenced map";
        };
    }
}
