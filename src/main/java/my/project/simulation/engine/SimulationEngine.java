package my.project.simulation.engine;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import my.project.gui.controllers.SimulationsController;
import my.project.gui.simulation.visualization.IVisualizer;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements IEngine, Runnable {
    private final Map<IMap, IVisualizer> visualizers = new HashMap<>();
    private final SimulationsController controller;

    public SimulationEngine(SimulationsController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        // TODO - Implement map refreshing with timeout with checking if visualization is paused
        initialize();

        while (true) { // TODO - add some finish criteria
            Thread frameThread = new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    Platform.runLater(controller::requestNewFrame);
                } catch (InterruptedException e) {
                    System.out.println("Symulacja została przerwana!");
                }
            });
            frameThread.start();
            try {
                frameThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void addData(IMap map, ScrollPane parentContainer) {
        visualizers.put(map, new SimulationVisualizer(map, parentContainer));
    }

    @Override
    public void removeData(IMap map) {
        visualizers.remove(map);
    }

    public void renderNewFrame() {
        for (IMap map: visualizers.keySet()) {
            map.update();
        }
    }

    private void initialize() {
        for (IMap map: visualizers.keySet()) {
            visualizers.get(map).drawGrid();
            map.initialize();
        }
    }
}
