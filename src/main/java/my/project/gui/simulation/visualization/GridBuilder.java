package my.project.gui.simulation.visualization;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import my.project.gui.events.ZoomHandler;
import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.enums.MapArea;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class GridBuilder {
    private static final String STEPPE_DIRT_PATH = "src/main/resources/images/steppe/steppe-dirt.jpg";
    private static final String JUNGLE_DIRT_PATH = "src/main/resources/images/jungle/jungle-dirt.jpg";
    private static final String GRID_CLASS = "gridPane";
    private static final int CONTAINER_SIZE = 500;
    private static final int CELL_SIZE = 50;

    private final IMap map;
    private final Vector2D mapLowerLeft;
    private final Vector2D mapUpperRight;

    private final GridPane gridPane = new GridPane();
    private final ScrollPane parentContainer;
    private int gridHeight;
    private int gridWidth;

    GridBuilder(IMap map, ScrollPane parentContainer) {
        this.map = map;
        this.parentContainer = parentContainer;
        List<Vector2D> mapBounds = map.getMapBoundingRect();
        this.mapLowerLeft = mapBounds.get(0);
        this.mapUpperRight = mapBounds.get(1);
        gridPane.getStyleClass().add(GRID_CLASS);
        map.setGridBuilder(this);
    }

    public int getCellSize() {
        return CELL_SIZE;
    }

    public void buildGrid() {
        // Get dimensions of a map
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapUpperRight.getY();

        // Store dimensions of a grid
        gridHeight = (maxY - minY + 2);
        gridWidth = (maxX - minX + 2);

        // Create columns
        for (int i = minX; i <= maxX + 1; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }

        // Create rows
        for (int i = minY; i <= maxY + 1; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        // Add columns numbers
        addColumnsNumbers(maxX - minX, maxY - minY);

        // Add rows numbers
        addRowsNumbers(maxX - minX, maxY - minY);

        // Show grid lines
        gridPane.setGridLinesVisible(true);
    }

    private void addColumnsNumbers(int width, int height) {
//        if (width % 2 == 1) {
//            for (int i = 0; i < width; i++) addNumLabel(i - width / 2, i + 1, height);
//        } else {
//            for (int i = 0; i < width / 2; i++) addNumLabel(i - width / 2, i + 1, height);
//            for (int i = width / 2; i < width; i++) addNumLabel(i - width / 2 + 1, 1 + i,height);
//        }
    }

    private void addRowsNumbers(int minY, int maxY) {

    }

    private void addLabel(String text, int x, int y) {
//        Label label = new Label(String.valueOf(num));
//        gridPane.add(label, x, y, 1, 1);
//        GridPane.setHalignment(label, HPos.CENTER);
    }

    public void loadGridTextures() {
        for (int i = 0; i <= mapUpperRight.getX(); i++) {
            for (int j = 0; j <= mapUpperRight.getY(); j++) {
                if (map.getAreaType(new Vector2D(i, j)) == MapArea.STEPPE) {
                    loadTexture(STEPPE_DIRT_PATH, i + 1, j);
                } else {
                    loadTexture(JUNGLE_DIRT_PATH, i + 1, j);
                }
            }
        }
    }

    // TODO - better error handling
    private void loadTexture(String imagePath, int x, int y) {
        try {
            Image image = new Image(new FileInputStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(CELL_SIZE);
            imageView.setFitWidth(CELL_SIZE);
            Pane pane = new Pane(imageView); // FIXME (adding borders to cells)
            pane.getStyleClass().add("cellTexture");
            gridPane.add(pane, x, y, 1, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void renderGrid() {
        Group innerGroup = new Group(gridPane);
        Group outerGroup = new Group(innerGroup);
        parentContainer.setPrefWidth(CONTAINER_SIZE);
        parentContainer.setPrefHeight(CONTAINER_SIZE);
        parentContainer.setContent(outerGroup);
        double scale = .99 * Math.min(1. * CONTAINER_SIZE / gridWidth, 1. * CONTAINER_SIZE / gridHeight) / CELL_SIZE;
        innerGroup.setScaleX(scale);
        innerGroup.setScaleY(scale);
        parentContainer.addEventFilter(ScrollEvent.ANY, new ZoomHandler(innerGroup, scale));
    }

    public void addSprite(IGuiSprite guiSprite) {
        Vector2D position = guiSprite.getPosition();
        int gridX = position.getX() + 1;
        int gridY = gridHeight - position.getY() - 2;
        System.out.println("Adding node: " + guiSprite.getNode() + " at position: " + guiSprite.getPosition());
        Platform.runLater(() -> gridPane.add(guiSprite.getNode(), gridX, gridY, 1, 1));
    }

    public void removeSprite(IGuiSprite guiSprite) {
        Platform.runLater(() -> gridPane.getChildren().remove(guiSprite.getNode()));
    }
}
