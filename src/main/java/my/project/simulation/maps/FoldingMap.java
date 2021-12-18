package my.project.simulation.maps;

import my.project.simulation.utils.Vector2D;

public class FoldingMap extends AbstractMap {
    FoldingMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector) {
        Vector2D newPosition = currPosition.add(moveVector);
        return newPosition.precedes(mapCornerVector) ? newPosition : currPosition;
    }
}
