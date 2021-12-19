package my.project.gui.simulation;

import javafx.scene.layout.Pane;
import my.project.simulation.maps.IMap;

public class SimulationVisualizer implements IVisualizer {
    private final GridBuilder builder;
    private boolean isPaused = true;

    public SimulationVisualizer(IMap map, Pane parentPane) {
        this.builder = new GridBuilder(map, parentPane);
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
    public void showNewFrame() {
        // TODO - add live data updates (charts, etc.)
        // (Don't implement any map sprites updates because they
        // will be updated automatically by their observers)
    }

    @Override
    public void drawGrid() {
        builder.buildGrid();
        builder.spawnSprites();
        builder.renderGrid();
    }

    public boolean isPaused() {
        return isPaused;
    }
}
