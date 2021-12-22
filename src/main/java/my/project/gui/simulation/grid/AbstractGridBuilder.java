package my.project.gui.simulation.grid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import my.project.gui.events.ZoomHandler;
import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.enums.MapArea;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGridBuilder implements IBuilder {
    protected static final String STEPPE_DIRT_PATH = "src/main/resources/images/steppe/steppe-dirt.jpg";
    protected static final String JUNGLE_DIRT_PATH = "src/main/resources/images/jungle/jungle-dirt.jpg";
    protected static final String NUMBER_CELL_CLASS = "cellNumber";
    protected static final String CELL_TEXTURE_CLASS = "cellTexture";
    protected static final String STEPPE_CLASS = "steppe";
    protected static final String JUNGLE_CLASS = "jungle";

    protected static final int CONTAINER_SIZE = 500;
    protected static final int PADDING_SIZE = 15;
    protected static final int CELL_SIZE = 50;
    protected static final int LABEL_FONT_SIZE = 20;
    protected static final int GRID_SEGMENT_SIZE = 5;

    protected final IMap map;
    protected final Vector2D jungleLowerLeft;
    protected final Vector2D jungleUpperRight;

    protected final GridPane wrapperGrid = new GridPane();
    protected final GridPane mapGrid = new GridPane();
    protected final List<List<StackPane>> mapGridCells = new ArrayList<>();
    private final ScrollPane parentContainer;

    protected int gridWidth;
    protected int gridHeight;
    protected int mapWidth;
    protected int mapHeight;

    AbstractGridBuilder(IMap map, ScrollPane parentContainer) {
        this.map = map;
        this.parentContainer = parentContainer;
        List<Vector2D> mapBounds = map.getMapBoundingRect();
        Vector2D mapLowerLeft = mapBounds.get(0);
        Vector2D mapUpperRight = mapBounds.get(1);
        this.jungleLowerLeft = mapBounds.get(2);
        this.jungleUpperRight = mapBounds.get(3);
        this.mapWidth = mapUpperRight.getX() - mapLowerLeft.getX() + 1;
        this.mapHeight = mapUpperRight.getY() - mapLowerLeft.getY() + 1;
        map.setGridBuilder(this);
    }

    @Override
    public int getCellSize() {
        return CELL_SIZE;
    }

    protected void buildMapGrid() {
        // Create wrapper columns
        for (int i = 0; i < mapWidth; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }
        // Create wrapper  rows
        for (int i = 0; i < mapWidth; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }
    }

    protected void addLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setFont(new Font(LABEL_FONT_SIZE));
        label.setText(text);
        VBox vBox = new VBox(label);
        vBox.setAlignment(Pos.BASELINE_CENTER);
        vBox.getStyleClass().add(NUMBER_CELL_CLASS);
        wrapperGrid.add(vBox, x, y, 1, 1);
    }

    public void loadBackground() {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                Vector2D mapPosition = new Vector2D(i, j);

                if (map.getAreaType(mapPosition) == MapArea.STEPPE) {
                    loadTexture(mapGrid, STEPPE_DIRT_PATH, mapPosition.getX(), mapHeight - mapPosition.getY() - 1,
                                CELL_SIZE, CELL_SIZE, CELL_TEXTURE_CLASS);
                } else {
                    loadTexture(mapGrid, JUNGLE_DIRT_PATH, mapPosition.getX(), mapHeight - mapPosition.getY() - 1,
                                CELL_SIZE, CELL_SIZE, CELL_TEXTURE_CLASS);
                }
            }
        }
    }

    // TODO - better error handling
    protected void loadTexture(GridPane gridPane, String imagePath, int x, int y, int width, int height, String className) {
        try {
            Image image = new Image(new FileInputStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(height);
            imageView.setFitWidth(width);
            StackPane stackPane = new StackPane(imageView);
            if (className != null) stackPane.getStyleClass().add(className);
            gridPane.add(stackPane, x, y, 1, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void renderGrid(int renderedWidth, int renderedHeight) {
        setupMapGridCells();
        StackPane innerContainer = new StackPane(wrapperGrid);
        Group outerContainer = new Group(innerContainer);

        parentContainer.setPrefWidth(CONTAINER_SIZE);
        parentContainer.setPrefHeight(CONTAINER_SIZE);
        innerContainer.setPadding(new Insets(PADDING_SIZE));
        innerContainer.setStyle("-fx-border-insets: " + PADDING_SIZE + "px");
        innerContainer.setStyle("-fx-background-insets: " + PADDING_SIZE + "px");
        parentContainer.setContent(outerContainer);

        double scale = .99 * CONTAINER_SIZE / Math.max(renderedWidth, renderedHeight);
        innerContainer.setScaleX(scale);
        innerContainer.setScaleY(scale);
        parentContainer.addEventFilter(ScrollEvent.ANY, new ZoomHandler(innerContainer, scale));
    }

    protected void setupMapGridCells() {
        for (int i = 0; i < mapWidth; i++) {
            mapGridCells.add(new ArrayList<>());
            for (int j = 0; j < mapHeight; j++) {
                StackPane stackPane = new StackPane();
                mapGridCells.get(i).add(stackPane);
                mapGrid.add(stackPane, i, mapHeight - j - 1, 1, 1);
            }
        }
    }

    @Override
    public void addSprite(IGuiSprite guiSprite) throws IllegalArgumentException {
        Vector2D position = guiSprite.getPosition();
        StackPane stackPane = mapGridCells.get(position.getX()).get(position.getY());
        stackPane.getChildren().add(guiSprite.getNode());
    }

    @Override
    public void removeSprite(IGuiSprite guiSprite) {
        Vector2D position = guiSprite.getPosition();
        StackPane stackPane = mapGridCells.get(position.getX()).get(position.getY());
        stackPane.getChildren().remove(guiSprite.getNode());
    }
}
