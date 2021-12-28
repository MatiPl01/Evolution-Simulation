package my.project.simulation.maps;

import my.project.gui.config.MapSettings;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.Vector2D;

public class FoldingMap extends AbstractMap {
    private static final String STATISTICS_FILE_NAME = "stats-foldingMap.csv";
    private static final String MAP_NAME = "Folding map";

    public FoldingMap(MapSettings mapSettings) {
        super(MAP_NAME, mapSettings);
        statsMeter = new StatsMeter(this, STATISTICS_FILE_NAME);
    }

    @Override
    public Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector) {
        Vector2D newPosition = currPosition.add(moveVector);
        // Get data about the map dimensions and the new expected position
        int x = newPosition.getX();
        int y = newPosition.getY();
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapUpperRight.getY();
        int mapWidth = maxX - minX + 1;
        int mapHeight = maxY - minY + 1;
        // Calculate newPosition coordinates
        if      (x > maxX) x %= mapWidth;
        else if (x < minX) x += mapWidth;
        if      (y > maxY) y %= mapHeight;
        else if (y < minY) y += mapHeight;
        // Return the new position vector
        return new Vector2D(x, y);
    }
}
