package my.project.simulation.maps;

import my.project.simulation.utils.Vector2D;

public class FoldingMap extends AbstractMap {
    FoldingMap(int width, int height, double jungleRatio, int bushEnergy, int grassEnergy) {
        super(width, height, jungleRatio, bushEnergy, grassEnergy);
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
