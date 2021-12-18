package my.project.simulation.maps;

import my.project.simulation.utils.Vector2D;

public class FencedMap extends AbstractMap {
    FencedMap(int width, int height, double jungleRatio, int bushEnergy, int grassEnergy) {
        super(width, height, jungleRatio, bushEnergy, grassEnergy);
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
