package my.project.gui.simulation.visualization;

import javafx.scene.control.ScrollPane;
import my.project.gui.simulation.grid.FencedMapGridBuilder;
import my.project.gui.simulation.grid.FoldingMapGridBuilder;
import my.project.gui.simulation.grid.IBuilder;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class SimulationVisualizer implements IVisualizer {
    private final IBuilder builder;
    private boolean isPaused = false;

    public SimulationVisualizer(IMap map, ScrollPane parentContainer) throws IllegalArgumentException {
        if (map instanceof FoldingMap) {
            this.builder = new FoldingMapGridBuilder(map, parentContainer);
        } else if (map instanceof FencedMap) {
            this.builder = new FencedMapGridBuilder(map, parentContainer);
        } else throw new IllegalArgumentException("There is no GridBuilder defined for map: " + map.getClass());
    }

    @Override
    public void startVisualization() {
        isPaused = false;
    }

    @Override
    public void pauseVisualization() {
        isPaused = true;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void drawGrid() {
        builder.buildGrid();
        builder.loadGridTextures();
        builder.renderGrid();
    }

    @Override
    public void showNewFrame() {
        // TODO - add live data updates (charts, etc.)
        // (Don't implement any map sprites updates because they
        // will be updated automatically by their observers)
    }
}
