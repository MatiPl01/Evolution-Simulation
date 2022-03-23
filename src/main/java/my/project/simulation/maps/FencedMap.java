package my.project.simulation.maps;

import my.project.gui.config.MapSettings;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.Vector2D;

public class FencedMap extends AbstractMap {
    private static final String STATISTICS_FILE_NAME = "stats-fencedMap.csv";
    private static final String MAP_NAME = "Fenced map";

    public FencedMap(MapSettings mapSettings) {
        super(MAP_NAME, mapSettings);
        statsMeter = new StatsMeter(this, STATISTICS_FILE_NAME);
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
