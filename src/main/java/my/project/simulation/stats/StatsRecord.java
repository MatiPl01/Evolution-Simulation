package my.project.simulation.stats;

public record StatsRecord(long aliveAnimalsCount,
                          long plantsCount,
                          double averageAliveEnergy,
                          double averageDiedLifespan,
                          double averageAliveChildren) {
    private static final String SEPARATOR = ",";

    @Override
    public String toString() {
        return aliveAnimalsCount + SEPARATOR +
               plantsCount + SEPARATOR +
               averageAliveEnergy + SEPARATOR +
               averageDiedLifespan + SEPARATOR +
               averageAliveChildren;
    }
}
