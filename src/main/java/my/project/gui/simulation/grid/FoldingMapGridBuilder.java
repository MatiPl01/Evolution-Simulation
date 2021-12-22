package my.project.gui.simulation.grid;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.util.List;

public class FoldingMapGridBuilder extends AbstractGridBuilder {

    public FoldingMapGridBuilder(IMap map, ScrollPane parentContainer) {
        super(map, parentContainer);
        // Store dimensions of a grid
        gridHeight = mapHeight + 1;
        gridWidth = mapWidth + 1;
    }

    public void buildGrid() {
        // Build grids
        buildMapGrid();
        buildWrapperGrid();
        // Add columns numbers
        addColumnsNumbers();
        // Add rows numbers
        addRowsNumbers();
        // Add grids to the wrapper grid
        wrapperGrid.add(mapGrid, 1, 0, mapWidth, mapHeight);
    }

    private void buildWrapperGrid() {
        // Create wrapper columns
        for (int i = 0; i < gridWidth; i++) {
            wrapperGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }
        // Create wrapper  rows
        for (int i = 0; i < gridHeight; i++) {
            wrapperGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }
    }

    protected void addColumnsNumbers() {
        if (mapWidth % 2 == 1) {
            for (int i = 0; i < mapWidth; i++) addLabel(String.valueOf(i - mapWidth / 2), i + 1, mapHeight);
        } else {
            for (int i = 0; i < mapWidth / 2; i++) addLabel(String.valueOf(i - mapWidth / 2), i + 1, mapHeight);
            for (int i = mapWidth / 2; i < mapWidth; i++) addLabel(String.valueOf(i - mapWidth / 2 + 1),  i + 1, mapHeight);
        }
    }

    protected void addRowsNumbers() {
        if (mapHeight % 2 == 1) {
            for (int i = 0; i < mapHeight; i++) addLabel(String.valueOf(i - mapHeight / 2), 0, mapHeight - i - 1);
        } else {
            for (int i = 0; i < mapHeight / 2; i++) addLabel(String.valueOf(i - mapHeight / 2), 0, mapHeight - i - 1);
            for (int i = mapHeight / 2; i < mapHeight; i++) addLabel(String.valueOf(i - mapHeight / 2 + 1),  0, mapHeight - i - 1);
        }
    }

    @Override
    public void renderGrid() {
        super.renderGrid(2 * PADDING_SIZE + CELL_SIZE * gridWidth, 2 * PADDING_SIZE + CELL_SIZE * gridHeight);
    }
}
