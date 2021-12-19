package my.project.gui.simulation;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.util.List;

class GridBuilder {
    private final GridPane gridPane = new GridPane();
    private final IMap map;
    private final Pane parentPane;

    GridBuilder(IMap map, Pane parentPane) {
        this.map = map;
        this.parentPane = parentPane;
    }

    public void buildGrid() {
        // Get dimensions of a grid
        List<Vector2D> vectors = map.getMapBoundingRect();
        Vector2D mapLowerLeft  = vectors.get(0);
        Vector2D mapUpperRight = vectors.get(1);
        Vector2D jungleLowerLeft  = vectors.get(2);
        Vector2D jungleUpperRight = vectors.get(3);

        int width = mapUpperRight.getX() - mapLowerLeft.getX() + 1;
        int height = mapUpperRight.getY() - mapLowerLeft.getY() + 1;

        System.out.println(parentPane.getBoundsInParent().getHeight());

        // Create columns
        for (int i = mapLowerLeft.getX(); i <= mapUpperRight.getX(); i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        }

        // Create rows
        for (int i = mapLowerLeft.getY(); i <= mapUpperRight.getY(); i++) {
            gridPane.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }

//        // Add columns numbers
//        addColumnsNumbers(mapLowerLeft.getX(), mapUpperRight.getX());
//
//        // Add rows numbers
//        addRowsNumbers(mapLowerLeft.getY(), mapUpperRight.getY());

        gridPane.setGridLinesVisible(true);
    }

//    private void addColumnsNumbers(int minX, int maxX) {
//        int width = maxX - minX + 1;
//        boolean isWidthOdd = width % 2 == 1;
//        if (isWidthOdd) {
//            for (int i = 0; i < width; i++) addNumLabel(i - width / 2, i + 1, 0);
//        } else {
//            for (int i = 0; i < width / 2; i++) addNumLabel(i, 0,0);
//            }
//            for (int i = 1; i <= maxX; i++) {
//                addNumLabel(i, 1 + i,0);
//            }
//        }
//    }
//
//    private void addRowsNumbers(int minY, int maxY) {
//
//    }

    private void addNumLabel(int num, int x, int y) {
        Label label = new Label(String.valueOf(num));
        gridPane.add(label, x, y, 1, 1);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    public void spawnSprites() {

    }

    public void renderGrid() {
        System.out.println("Added grid to: " + parentPane);
        parentPane.getChildren().add(gridPane);
    }
}
