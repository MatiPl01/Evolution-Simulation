package my.project.simulation.engine;

import javafx.application.Platform;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.gui.enums.SimulationState;
import my.project.simulation.maps.IMap;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements IEngine, Runnable {
    private final SimulationVisualizer visualizer;
    private final IMap map;
    private SimulationState simulationState = SimulationState.PAUSED;

    private final static int SLEEP_TIME = 100; // When simulation is paused
    private int refreshInterval = 300;

    public SimulationEngine(IMap map, SimulationVisualizer visualizer) {
        this.visualizer = visualizer;
        this.map = map;
    }

    @Override
    public void start() {
        this.simulationState = SimulationState.RUNNING;
    }

    @Override
    public void pause() {
        this.simulationState = SimulationState.PAUSED;
    }

    @Override
    public SimulationState getState() {
        return this.simulationState;
    }

    @Override
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    @Override
    public void run() {
        // Initialize maps and wait for them to render completely
        boolean wasInitializationSuccessful = initialize();
        System.out.println("Initialized successfully? " + wasInitializationSuccessful);
        if (!wasInitializationSuccessful) return;

        // Start rendering simulation frames
        while (true) {
            switch (simulationState) {
                case RUNNING -> {
                    boolean wasFrameRendered = renderNewFrame();
                    if (!wasFrameRendered) return;
                }
                case PAUSED -> sleep(SLEEP_TIME);
                case FINISHED -> {
                    return;
                }
            }
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
        sleep(Math.max(0, refreshInterval - (int)(endTime - startTime)));
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
