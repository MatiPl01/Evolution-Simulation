package my.project.simulation.maps;

import my.project.simulation.utils.Vector2D;

public class FencedMap extends AbstractMap {
    FencedMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector) {
        Vector2D newPosition = currPosition.add(moveVector);
        int x = currPosition.getX();
        int y = currPosition.getY();
        if (Math.abs(newPosition.getX()) >= mapCornerVector.getX()) x *= -1;
        if (Math.abs(newPosition.getY()) >= mapCornerVector.getY()) y *= -1;
        return new Vector2D(x, y);
    }
}
