package my.project.simulation.engine;

import my.project.gui.simulation.IVisualizer;
import my.project.gui.simulation.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.HashMap;
import java.util.Map;

public class SimulationEngine implements IEngine, Runnable {
    private final Map<IMap, IVisualizer> visualizers = new HashMap<>();

    @Override
    public void run() {
        // Implement map refreshing with timeout
    }

    public void addVisualizer(IMap map) {
        visualizers.put(map, new SimulationVisualizer(map));
    }

    public void removeVisualizer(IMap map) {
        visualizers.remove(map);
    }

    public void requestNewFrame() {

    }
}
