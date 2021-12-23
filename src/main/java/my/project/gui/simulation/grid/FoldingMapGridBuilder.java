package my.project.gui.simulation.grid;

import javafx.scene.layout.*;
import my.project.simulation.maps.IMap;

public class FoldingMapGridBuilder extends AbstractGridBuilder {

    public FoldingMapGridBuilder(IMap map, AnchorPane parentContainer) {
        super(map, parentContainer);
        // Store dimensions of a grid
        gridHeight = mapHeight + 1;
        gridWidth = mapWidth + 1;
    }

    public void buildGrid() {
        setupBackground();
        // Create columns
        for (int i = 0; i < gridWidth; i++) {
            wrapperGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }
        // Create rows
        for (int i = 0; i < gridHeight; i++) {
            wrapperGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }
        // Add columns numbers
        addColumnsNumbers();
        // Add rows numbers
        addRowsNumbers();
        // Add grids to the wrapper grid
        wrapperGrid.add(backgroundPane, 1, 0, mapWidth, mapHeight);
        wrapperGrid.add(plantsGrid, 1, 0, mapWidth, mapHeight);
        wrapperGrid.add(animalsGrid, 1, 0, mapWidth, mapHeight);
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
