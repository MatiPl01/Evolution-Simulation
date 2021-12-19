package my.project.simulation.engine;

import javafx.scene.control.ScrollPane;
import my.project.gui.simulation.visualization.IVisualizer;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.HashMap;
import java.util.Map;

public class SimulationEngine implements IEngine, Runnable {
    private final Map<IMap, IVisualizer> visualizers = new HashMap<>();

    @Override
    public void run() {
        // TODO - Implement map refreshing with timeout with checking if visualization is paused
        initialize();
    }

    @Override
    public void addData(IMap map, ScrollPane parentContainer) {
        visualizers.put(map, new SimulationVisualizer(map, parentContainer));
    }

    @Override
    public void removeData(IMap map) {
        visualizers.remove(map);
    }

    private void initialize() {
        for (IMap map: visualizers.keySet()) {
            visualizers.get(map).drawGrid();
            map.initialize();
        }
    }

    private void requestNewFrame() {
        for (IMap map: visualizers.keySet()) {
            map.update();
        }
    }
}
