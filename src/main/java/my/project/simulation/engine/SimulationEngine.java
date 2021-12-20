package my.project.simulation.engine;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import my.project.gui.simulation.visualization.IVisualizer;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements IEngine, Runnable {
    public static final int INITIALIZATION_CHECK_INTERVAL = 100;
    private final Map<IVisualizer, IMap> visualizers = new HashMap<>();
    private final Set<IVisualizer> activeVisualizers = new HashSet<>();
    private boolean initializationDone = false;

    @Override
    public void run() {
        Platform.runLater(this::initialize);
        // TODO - Implement map refreshing with timeout with checking if visualization is paused
        try {
            while (!initializationDone) sleep(INITIALIZATION_CHECK_INTERVAL);
            while (!finishedAllVisualizations()) {
                Platform.runLater(this::renderNewFrame);
                sleep(100); // TODO - make me adjustable in GUI
            }
        } catch (InterruptedException e) {
            System.out.println("Simulation was interrupted");
            e.printStackTrace();
        }
    }

    @Override
    public void addData(IMap map, ScrollPane parentContainer) {
        IVisualizer visualizer = new SimulationVisualizer(map, parentContainer);
        visualizers.put(visualizer, map);
        activeVisualizers.add(visualizer);
    }

    @Override
    public void renderNewFrame() {
        Iterator<IVisualizer> it = activeVisualizers.iterator();
        while (it.hasNext()) {
            IVisualizer visualizer = it.next();
            if (!visualizer.isPaused()) {
                IMap map = visualizers.get(visualizer);
                map.update();
                // Deactivate a visualizer if there are no more animals alive
                if (!map.areAnimalsAlive()) it.remove();
            }
        }
    }

    private void initialize() {
        for (IVisualizer visualizer: visualizers.keySet()) {
            visualizer.drawGrid();
            visualizers.get(visualizer).initialize();
        }
        initializationDone = true;
    }

    private void sleep(int milliseconds) throws InterruptedException {
        Thread sleepingThread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                System.out.println("Sleeping interrupted");
                e.printStackTrace();
            }
        });
        sleepingThread.start();
        sleepingThread.join();
    }

    private boolean finishedAllVisualizations() {
        return activeVisualizers.size() == 0;
    }
}
