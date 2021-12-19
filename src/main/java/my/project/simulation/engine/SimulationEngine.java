package my.project.simulation.engine;

import javafx.scene.layout.Pane;
import my.project.gui.simulation.IVisualizer;
import my.project.gui.simulation.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.HashMap;
import java.util.Map;

public class SimulationEngine implements IEngine, Runnable {
    private final Map<IMap, IVisualizer> visualizers = new HashMap<>();

    @Override
    public void run() {
        // TODO - Implement map refreshing with timeout with checking if visualization is paused
        for (IVisualizer visualizer: visualizers.values()) {
            visualizer.drawGrid();
        }
    }

    @Override
    public void addData(IMap map, Pane parentPane) {
        visualizers.put(map, new SimulationVisualizer(map, parentPane));
    }

    @Override
    public void removeData(IMap map) {
        visualizers.remove(map);
    }

    private void requestNewFrame() {
        for (IMap map: visualizers.keySet()) {
            map.update();
        }
    }
}
