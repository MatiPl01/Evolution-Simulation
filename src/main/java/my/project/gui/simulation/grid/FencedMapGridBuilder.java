package my.project.gui.simulation.grid;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public class FencedMapGridBuilder extends AbstractGridBuilder{
    protected static final String FENCE_TOP_PATH = "src/main/resources/images/fence/fence-top.jpg";
    protected static final String FENCE_LEFT_PATH = "src/main/resources/images/fence/fence-left.jpg";
    protected static final String FENCE_RIGHT_PATH = "src/main/resources/images/fence/fence-right.jpg";
    protected static final String FENCE_BOTTOM_PATH = "src/main/resources/images/fence/fence-bottom.jpg";
    protected static final String FENCE_TOP_LEFT_PATH = "src/main/resources/images/fence/fence-top-left.jpg";
    protected static final String FENCE_TOP_RIGHT_PATH = "src/main/resources/images/fence/fence-top-right.jpg";
    protected static final String FENCE_BOTTOM_LEFT_PATH = "src/main/resources/images/fence/fence-bottom-left.jpg";
    protected static final String FENCE_BOTTOM_RIGHT_PATH = "src/main/resources/images/fence/fence-bottom-right.jpg";

    private static final int FENCE_WIDTH = CELL_SIZE / 2;

    public FencedMapGridBuilder(IMap map, ScrollPane parentContainer) {
        super(map, parentContainer);

        // Get dimensions of a map
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapUpperRight.getY();

        // Store dimensions of a grid
        gridHeight = (maxY - minY + 4);
        gridWidth = (maxX - minX + 4);
    }

    public void buildGrid() {
        // Create columns
        for (int i = 0; i < gridWidth; i++) {
            if (i == 1 || i == gridWidth - 1) gridPane.getColumnConstraints().add(new ColumnConstraints(FENCE_WIDTH));
            else gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }

        // Create rows
        for (int i = 0; i < gridHeight; i++) {
            if (i == 0 || i == gridHeight - 2) gridPane.getRowConstraints().add(new RowConstraints(FENCE_WIDTH));
            else gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        // Add columns numbers
        addColumnsNumbers();

        // Add rows numbers
        addRowsNumbers();
    }

    @Override
    protected void addColumnsNumbers() {
        int width = gridWidth - 3;
        if (width % 2 == 1) {
            for (int i = 0; i < width; i++) addLabel(String.valueOf(i - width / 2), i + 2, gridHeight - 1);
        } else {
            for (int i = 0; i < width / 2; i++) addLabel(String.valueOf(i - width / 2), i + 2, gridHeight - 1);
            for (int i = width / 2; i < width; i++) addLabel(String.valueOf(i - width / 2 + 1),  i + 2, gridHeight - 1);
        }
    }

    @Override
    protected void addRowsNumbers() {
        int height = gridHeight - 3;
        if (height % 2 == 1) {
            for (int i = 0; i < height; i++) addLabel(String.valueOf(i - height / 2), 0, gridHeight - i - 3);
        } else {
            for (int i = 0; i < height / 2; i++) addLabel(String.valueOf(i - height / 2), 0, gridHeight - i - 3);
            for (int i = height / 2; i < height; i++) addLabel(String.valueOf(i - height / 2 + 1),  0, gridHeight - i - 3);
        }
    }

    @Override
    public Vector2D getGridPosition(Vector2D position) {
        return new Vector2D(position.getX() + 2, gridHeight - position.getY() - 3);
    }

    @Override
    public void renderGrid() {
        renderGrid(2 * PADDING_SIZE + CELL_SIZE * (gridWidth - 2) + 2 * FENCE_WIDTH,
                2 * PADDING_SIZE + CELL_SIZE * (gridHeight - 2) + 2 * FENCE_WIDTH);
    }

    @Override
    public void loadGridTextures() {
        super.loadGridTextures();
        loadFenceTextures();
    }

    private void loadFenceTextures() {
        // Add horizontal fences
        for (int i = 2; i < gridWidth - 1; i++) {
            loadTexture(FENCE_BOTTOM_PATH, i, gridHeight - 2, CELL_SIZE, FENCE_WIDTH, null);
            loadTexture(FENCE_TOP_PATH, i, 0, CELL_SIZE, FENCE_WIDTH, null);
        }
        // Add vertical fences
        for (int i = 1; i < gridHeight - 2; i++) {
            loadTexture(FENCE_LEFT_PATH, 1, i, FENCE_WIDTH, CELL_SIZE, null);
            loadTexture(FENCE_RIGHT_PATH, gridWidth - 1, i, FENCE_WIDTH, CELL_SIZE, null);
        }
        // Corners
        loadTexture(FENCE_TOP_LEFT_PATH, 1, 0, FENCE_WIDTH, FENCE_WIDTH, null);
        loadTexture(FENCE_TOP_RIGHT_PATH, gridWidth - 1, 0, FENCE_WIDTH, FENCE_WIDTH, null);
        loadTexture(FENCE_BOTTOM_LEFT_PATH, 1, gridHeight - 2, FENCE_WIDTH, FENCE_WIDTH, null);
        loadTexture(FENCE_BOTTOM_RIGHT_PATH, gridWidth - 1, gridHeight - 2, FENCE_WIDTH, FENCE_WIDTH, null);
    }
}
