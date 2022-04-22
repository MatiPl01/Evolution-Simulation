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

    @Override
    public long aliveAnimalsCount() {
        return aliveAnimalsCount;
    }

    @Override
    public long plantsCount() {
        return plantsCount;
    }

    @Override
    public double averageAliveEnergy() {
        return averageAliveEnergy;
    }

    @Override
    public double averageDiedLifespan() {
        return averageDiedLifespan;
    }

    @Override
    public double averageAliveChildren() {
        return averageAliveChildren;
    }

    public String getSeparator() {
        return SEPARATOR;
    }

    public StatsRecord add(StatsRecord other) {
        return new StatsRecord(
                aliveAnimalsCount + other.aliveAnimalsCount,
                plantsCount + other.plantsCount,
                averageAliveEnergy + other.averageAliveEnergy,
                averageDiedLifespan + other.averageDiedLifespan,
                averageAliveChildren + other.averageAliveChildren
        );
    }
}
