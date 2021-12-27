package my.project.simulation.engine;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.enums.SimulationState;
import my.project.simulation.maps.IMap;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements IEngine, Runnable {
    private final SimulationVisualizer visualizer;
    private final IMap map;
    private SimulationState simulationState = SimulationState.RUNNING;

    private final static int SLEEP_TIME = 100; // When simulation is paused
    private final static int refreshTime = 1; // TODO - make me adjustable in GUI

    public SimulationEngine(IMap map, AnchorPane parentContainer) {
        this.visualizer = new SimulationVisualizer(map, parentContainer);
        this.map = map;
    }

    @Override
    public void run() {
        // Initialize maps and wait for them to render completely
        boolean wasInitializationSuccessful = initialize();
        System.out.println("Initialized successfully? " + wasInitializationSuccessful);
        if (!wasInitializationSuccessful) return;

        // TODO - remove code below
        int i = 0;
        // TODO - remove code above

        // Start rendering simulation frames
        while (true) {
            switch (simulationState) {
                case RUNNING -> {
                    boolean wasFrameRendered = renderNewFrame();
                    if (!wasFrameRendered) return;
//                     TODO - remove code below
                    i++;
                    if (i % 5000 == 0) {
                        visualizer.pauseVisualization();
                        visualizer.showDominantGenomesAnimals();
                        sleep(5000);
                        visualizer.hideDominantGenomesAnimals();
                        visualizer.startVisualization();
                    }
//                     TODO - remove code above
                }
                case PAUSED -> sleep(SLEEP_TIME);
                case FINISHED -> {
                    return;
                }
            }
            sleep(refreshTime);
        }

        // TODO - move statistics saving to another place (only if user decides to generate a file with statistics)
//        for (IMap map: visualizers.values()) map.getStatsMeter().generateCSVFile();
    }

    private boolean initialize() {
        // Setup map initializer
        FutureTask<Void> future = new FutureTask<>(() -> initializeVisualizer(visualizer), null);
        Platform.runLater(future);
        // Wait for initializer to finish
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void initializeVisualizer(SimulationVisualizer visualizer) {
        visualizer.drawGrid();
        map.initialize();
    }

    private boolean renderNewFrame() {
        long startTime = System.currentTimeMillis();
        // Setup a map new frame rendering
        FutureTask<Void> future = new FutureTask<>(() -> {
            map.update();
            if (!map.areAnimalsAlive()) simulationState = SimulationState.FINISHED;
        }, null);
        Platform.runLater(future);
        // Wait until a map finished rendering the current frame
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        long endTime = System.currentTimeMillis();
        // Add some delay is tasks were finished to early
        sleep(Math.max(0, refreshTime - (int)(endTime - startTime)));
        return true;
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
}
