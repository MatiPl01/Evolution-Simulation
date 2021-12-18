package my.project.simulation.maps;

import my.project.simulation.utils.Vector2D;

public class FoldingMap extends AbstractMap {
    FoldingMap(int totalWidth, int totalHeight, int jungleWidth, int jungleHeight) {
        super(totalWidth, totalHeight, jungleWidth, jungleHeight);
    }

    @Override
    public Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector) {
        return null;
    }
}
