package my.project.gui.simulation.grid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import my.project.gui.components.ZoomableScrollPane;
import my.project.gui.controllers.AbstractContainerController;
import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.maps.IMap;
import my.project.simulation.sprites.AbstractPlant;
import my.project.simulation.sprites.Animal;
import my.project.simulation.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGridBuilder implements IBuilder {
    protected static final String NUMBER_CELL_CLASS = "cellNumber";
    protected static final String JUNGLE_AREA_CLASS = "jungleArea";
    protected static final String STEPPE_AREA_CLASS = "steppeArea";
    protected static final String LINES_GRID_CLASS = "gridLines";

    protected static final int PADDING_SIZE = 15;
    protected static final int CELL_SIZE = 50;
    protected static final int LABEL_FONT_SIZE = 20;

    protected final IMap map;
    protected final Vector2D jungleLowerLeft;
    protected final Vector2D jungleUpperRight;

    protected final List<List<StackPane>> spritesGridHelper = new ArrayList<>();
    protected final GridPane wrapperGrid = new GridPane();
    protected final GridPane spritesGrid = new GridPane();
    protected final GridPane linesGrid = new GridPane();
    protected final StackPane backgroundPane = new StackPane();
    protected final AnchorPane parentContainer;
    protected AbstractContainerController controller;

    protected int gridWidth;
    protected int gridHeight;
    protected int mapWidth;
    protected int mapHeight;

    AbstractGridBuilder(IMap map, AnchorPane parentContainer) {
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

    protected abstract void buildGrid();

    protected abstract void renderGrid();

    @Override
    public int getCellSize() {
        return CELL_SIZE;
    }

    @Override
    public void initialize() {
        setupSpritesGrid();
        setupLinesGrid();
        buildGrid();
    }

    @Override
    public void render() {
        renderGrid();
    }

    private void setupSpritesGrid() {
        setupGrid(spritesGrid, mapWidth, mapHeight);

        for (int x = 0; x < mapWidth; x++) {
            spritesGridHelper.add(new ArrayList<>());
            for (int y = 0; y < mapHeight; y++) {
                StackPane stackPane = new StackPane();
                spritesGridHelper.get(x).add(stackPane);
                spritesGrid.add(stackPane, x, mapHeight - y - 1, 1, 1);

                // Add an event handler
                Vector2D position = new Vector2D(x, y);
                stackPane.setOnMouseClicked(event -> this.controller.notifyClick(position));
            }
        }
    }

    private void setupLinesGrid() {
        setupGrid(linesGrid, mapWidth, mapHeight);
        displayGridLines(linesGrid);
    }

    private void setupGrid(GridPane gridPane, int width, int height) {
        for (int i = 0; i < height; i++) gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        for (int i = 0; i < width; i++)  gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
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

    protected void setupBackground() {
        backgroundPane.getStyleClass().add(STEPPE_AREA_CLASS);
        StackPane junglePane = new StackPane();
        junglePane.getStyleClass().add(JUNGLE_AREA_CLASS);
        junglePane.setMaxHeight((jungleUpperRight.getY() - jungleLowerLeft.getY() + 1) * CELL_SIZE);
        junglePane.setMaxWidth((jungleUpperRight.getX() - jungleLowerLeft.getX() + 1) * CELL_SIZE);
        backgroundPane.getChildren().add(junglePane);
    }

    protected void renderGrid(int renderedWidth, int renderedHeight) {
        double parentHeight  = parentContainer.getBoundsInParent().getHeight();
        double parentWidth   = parentContainer.getBoundsInParent().getWidth();
        double containerSize = Math.min(parentHeight, parentWidth);

        wrapperGrid.setPadding(new Insets(PADDING_SIZE));
        wrapperGrid.setStyle("-fx-border-insets: " + PADDING_SIZE + "px");
        wrapperGrid.setStyle("-fx-background-insets: " + PADDING_SIZE + "px");

        double initialScale = .95 * containerSize / Math.max(renderedWidth, renderedHeight);

        ScrollPane scrollPane = new ZoomableScrollPane(wrapperGrid, initialScale);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        parentContainer.getChildren().add(scrollPane);
    }

    @Override
    public void addSprite(IGuiSprite guiSprite) {
        Vector2D position = guiSprite.getPosition();
        spritesGridHelper.get(position.getX()).get(position.getY()).getChildren().add(guiSprite.getNode());
    }

    @Override
    public void removeSprite(IGuiSprite guiSprite) {
        Vector2D position = guiSprite.getPosition();
        spritesGridHelper.get(position.getX()).get(position.getY()).getChildren().remove(guiSprite.getNode());
    }

    @Override
    public void setEventsController(AbstractContainerController controller) {
        this.controller = controller;
    }

    private void displayGridLines(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.getStyleClass().add(LINES_GRID_CLASS);
    }

    protected void loadTexture(GridPane gridPane, Image image, int x, int y, int width, int height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        StackPane stackPane = new StackPane(imageView);
        gridPane.add(stackPane, x, y, 1, 1);
    }
}
