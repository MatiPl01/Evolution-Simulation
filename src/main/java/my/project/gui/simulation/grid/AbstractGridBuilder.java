package my.project.gui.simulation.grid;

import javafx.application.Platform;
import javafx.geometry.HPos;
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
import java.util.List;

public abstract class AbstractGridBuilder implements IBuilder {
    protected static final String STEPPE_DIRT_PATH = "src/main/resources/images/steppe/steppe-dirt.jpg";
    protected static final String JUNGLE_DIRT_PATH = "src/main/resources/images/jungle/jungle-dirt.jpg";
    protected static final String GRID_CLASS = "gridPane";
    public static final String CELL_LABEL_CLASS = "cellLabel";
    public static final String CELL_TEXTURE_CLASS = "cellTexture";
    protected static final int CONTAINER_SIZE = 500;
    protected static final int PADDING_SIZE = 15;
    protected static final int CELL_SIZE = 50;
    protected static final int LABEL_FONT_SIZE = 20;

    protected final IMap map;
    protected final Vector2D mapLowerLeft;
    protected final Vector2D mapUpperRight;

    protected final GridPane gridPane = new GridPane();
    protected final ScrollPane parentContainer;
    protected int gridHeight;
    protected int gridWidth;

    AbstractGridBuilder(IMap map, ScrollPane parentContainer) {
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

    protected void addLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setFont(new Font(LABEL_FONT_SIZE));
        label.setText(text);
        VBox vBox = new VBox(label);
        vBox.setAlignment(Pos.BASELINE_CENTER);
        vBox.getStyleClass().add(CELL_LABEL_CLASS);
        gridPane.add(vBox, x, y, 1, 1);
    }

    public void loadGridTextures() {
        for (int i = 0; i <= mapUpperRight.getX(); i++) {
            for (int j = 0; j <= mapUpperRight.getY(); j++) {
                Vector2D mapPosition = new Vector2D(i, j);
                Vector2D gridPosition = getGridPosition(mapPosition);
                if (map.getAreaType(mapPosition) == MapArea.STEPPE) {
                    loadTexture(STEPPE_DIRT_PATH, gridPosition.getX(), gridPosition.getY(),
                                CELL_SIZE, CELL_SIZE, CELL_LABEL_CLASS);
                } else {
                    loadTexture(JUNGLE_DIRT_PATH, gridPosition.getX(), gridPosition.getY(),
                                CELL_SIZE, CELL_SIZE, CELL_LABEL_CLASS);
                }
            }
        }
    }

    // TODO - better error handling
    protected void loadTexture(String imagePath, int x, int y, int width, int height, String className) {
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
        StackPane innerContainer = new StackPane(gridPane);
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

    public void addSprite(IGuiSprite guiSprite) {
        Vector2D gridPosition = getGridPosition(guiSprite.getPosition());
        gridPane.add(guiSprite.getNode(), gridPosition.getX(), gridPosition.getY(), 1, 1);
    }

    public void removeSprite(IGuiSprite guiSprite) {
        gridPane.getChildren().remove(guiSprite.getNode());
    }

    abstract Vector2D getGridPosition(Vector2D position);

    abstract void addColumnsNumbers();

    abstract void addRowsNumbers();
}
