package my.project.simulation.maps;

import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.Vector2D;

public class FencedMap extends AbstractMap {
    private static final String STATISTICS_FILE_NAME = "stats-fencedMap.csv";

    public FencedMap(int width, int height, double jungleRatio,
                     int startEnergy, int moveEnergy, int bushEnergy, int grassEnergy,
                     int animalsCount) {
        super(width, height, jungleRatio, startEnergy, moveEnergy, bushEnergy, grassEnergy, animalsCount);
        statsMeter = new StatsMeter(STATISTICS_FILE_NAME);
    }

    @Override
    public Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector) {
        Vector2D newPosition = currPosition.add(moveVector);
        if (newPosition.follows(mapLowerLeft) && newPosition.precedes(mapUpperRight)) {
            return newPosition;
        }
        return currPosition;
    }
}
