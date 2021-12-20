package my.project.gui.simulation.grid;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public class FoldingMapGridBuilder extends AbstractGridBuilder {

    public FoldingMapGridBuilder(IMap map, ScrollPane parentContainer) {
        super(map, parentContainer);

        // Get dimensions of a map
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapUpperRight.getY();

        // Store dimensions of a grid
        gridHeight = (maxY - minY + 2);
        gridWidth = (maxX - minX + 2);
    }

    public void buildGrid() {
        // Create columns
        for (int i = 0; i < gridWidth; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }

        // Create rows
        for (int i = 0; i < gridHeight; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        // Add columns numbers
        addColumnsNumbers();

        // Add rows numbers
        addRowsNumbers();
    }

    @Override
    protected void addColumnsNumbers() {
        int width = gridWidth - 1;
        int height = gridHeight - 1;
        if (width % 2 == 1) {
            for (int i = 0; i < width; i++) addLabel(String.valueOf(i - width / 2), i + 1, height);
        } else {
            for (int i = 0; i < width / 2; i++) addLabel(String.valueOf(i - width / 2), i + 1, height);
            for (int i = width / 2; i < width; i++) addLabel(String.valueOf(i - width / 2 + 1),  i + 1, height);
        }
    }

    @Override
    protected void addRowsNumbers() {
        int height = gridHeight - 1;
        if (height % 2 == 1) {
            for (int i = 0; i < height; i++) addLabel(String.valueOf(i - height / 2), 0, height - i - 1);
        } else {
            for (int i = 0; i < height / 2; i++) addLabel(String.valueOf(i - height / 2), 0, height - i - 1);
            for (int i = height / 2; i < height; i++) addLabel(String.valueOf(i - height / 2 + 1),  0, height - i - 1);
        }
    }

    @Override
    public Vector2D getGridPosition(Vector2D position) {
        return new Vector2D(position.getX() + 1, gridHeight - position.getY() - 2);
    }

    @Override
    public void renderGrid() {
        renderGrid(2 * PADDING_SIZE + CELL_SIZE * gridWidth, 2 * PADDING_SIZE + CELL_SIZE * gridHeight);
    }
}
