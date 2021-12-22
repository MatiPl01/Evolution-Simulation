package my.project.simulation.engine;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.maps.IMap;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements IEngine, Runnable {
    private final Map<SimulationVisualizer, IMap> visualizers = new HashMap<>();
    private final Set<SimulationVisualizer> activeVisualizers = new HashSet<>();

    private final static int refreshTime = 1; // TODO - make me adjustable in GUI

    @Override
    public void run() {
        // Initialize maps and wait for them to render completely
        try {
            initialize();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("An error has occurred during maps initialization");
            e.printStackTrace();
        }
        // Set some timeout to ensure that everything is displayed
        sleep(10000);// TODO - remove me after implementing animation start via gui

        // TODO - remove code below
        int i = 0;
        // TODO - remove code above

        // Start rendering simulation frames
        while (!finishedAllVisualizations()) {
            try {
                renderNewFrames();

                // TODO - remove code below
                i++;
                if (i % 100 == 0) {
                    activeVisualizers.forEach(SimulationVisualizer::pauseVisualization);
                    activeVisualizers.forEach(SimulationVisualizer::showDominantGenomesAnimals);
                    sleep(4000);
                    activeVisualizers.forEach(SimulationVisualizer::hideDominantGenomesAnimals);
                    activeVisualizers.forEach(SimulationVisualizer::startVisualization);
                }
                // TODO - remove code above
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Simulation was interrupted");
                e.printStackTrace();
                return;
            }
        }
        // TODO - move statistics saving to another place (only if user decides to generate a file with statistics)
        for (IMap map: visualizers.values()) map.getStatsMeter().generateCSVFile();
    }

    @Override
    public void addData(IMap map, ScrollPane parentContainer) {
        SimulationVisualizer visualizer = new SimulationVisualizer(map, parentContainer);
        visualizers.put(visualizer, map);
        activeVisualizers.add(visualizer);
    }

    private void initialize() throws ExecutionException, InterruptedException {
        // Setup all maps initializers
        List<FutureTask<Void>> futures = new ArrayList<>();
        for (SimulationVisualizer visualizer: visualizers.keySet()) {
            FutureTask<Void> future = new FutureTask<>(() -> initializeVisualizer(visualizer), null);
            futures.add(future);
            Platform.runLater(future);
        }
        // Wait for all initializers to finish
        for (FutureTask<Void> future: futures) {
            future.get();
        }
    }

    private void initializeVisualizer(SimulationVisualizer visualizer) {
        visualizer.drawGrid();
        visualizers.get(visualizer).initialize();
    }

    private void renderNewFrames() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        // Setup all maps new frame rendering
        List<FutureTask<Void>> futures = new ArrayList<>();
        for (SimulationVisualizer visualizer: activeVisualizers) {
            FutureTask<Void> future = new FutureTask<>(() -> renderNewFrame(visualizer), null);
            futures.add(future);
            Platform.runLater(future);
        }
        // Wait until all maps finished rendering the current frame
        for (FutureTask<Void> future: futures) future.get();
        long endTime = System.currentTimeMillis();
        // Add some delay is tasks were finished to early
        sleep(Math.max(0, refreshTime - (int)(endTime - startTime)));
    }

    private void renderNewFrame(SimulationVisualizer visualizer) {
        if (!visualizer.isPaused()) {
            IMap map = visualizers.get(visualizer);
            map.update();
            // Deactivate a visualizer if there are no more animals alive
            if (!map.areAnimalsAlive()) activeVisualizers.remove(visualizer);
        }
    }

    private void sleep(int milliseconds) {
        try {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Sleeping interrupted");
        }
    }

    private boolean finishedAllVisualizations() {
        return activeVisualizers.size() == 0;
    }
}
