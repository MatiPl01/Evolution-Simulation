package my.project.gui.simulation;

import my.project.simulation.maps.IMap;

public class SimulationVisualizer implements IVisualizer {
    private final Drawer drawer;
    private boolean isPaused = true;

    public SimulationVisualizer(IMap map) {
        this.drawer = new Drawer(map);
    }

    public void initialize() {
        drawer.drawInitialGrid();
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void startVisualization() {
        isPaused = false;
    }

    @Override
    public void pauseVisualization() {
        isPaused = true;
    }
}
