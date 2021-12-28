package my.project.gui.controllers;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import my.project.gui.charts.ChartDrawer;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.enums.SimulationState;
import my.project.simulation.maps.IMap;

import java.util.concurrent.atomic.AtomicReference;

public class AbstractContainerController {
    private static final int DEFAULT_REFRESH_INTERVAL = 300; // ms
    private static final int MIN_REFRESH_INTERVAL = 1;    // ms
    private static final int MAX_REFRESH_INTERVAL = 1000; // ms

    private AnchorPane simulationBox;
    private AnchorPane chartBox;
    private Slider refreshSlider;
    private Label refreshLabel;
    protected Side legendSide;

    private IEngine engine;

    public void setSimulationBox(AnchorPane simulationBox) {
        this.simulationBox = simulationBox;
    }

    public void setChartBox(AnchorPane chartBox) {
        this.chartBox = chartBox;
    }

    public void setRefreshSlider(Slider refreshSlider) {
        this.refreshSlider = refreshSlider;
    }

    public void setRefreshLabel(Label refreshLabel) {
        this.refreshLabel = refreshLabel;
    }

    public void launch(IMap map) {
        map.setChartDrawer(new ChartDrawer(this.chartBox, null, "Day", "Count", legendSide));
        SimulationVisualizer simulationVisualizer = new SimulationVisualizer(map, simulationBox);
        this.engine = new SimulationEngine(map, simulationVisualizer);
        Thread engineThread = new Thread((Runnable) this.engine);
        setupRefreshInterval();
        engineThread.start();
    }

    protected void pauseButtonClicked(Button pauseButton) {
        if (engine.getState() == SimulationState.PAUSED) {
            engine.start();
            pauseButton.setText("Pause");
        } else {
            engine.pause();
            pauseButton.setText("Start");
        }
    }

    private void setupRefreshInterval() {
        double ratio = 1. * (DEFAULT_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL)
                / (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL);
        refreshSlider.setValue(100. * (1 - ratio));
        refreshLabel.setText(String.valueOf(DEFAULT_REFRESH_INTERVAL));
        this.engine.setRefreshInterval(DEFAULT_REFRESH_INTERVAL);

        refreshSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int refreshInterval = (int) ((1 - refreshSlider.getValue() / 100)
                    * (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL) + MIN_REFRESH_INTERVAL);
            refreshLabel.setText(String.valueOf(refreshInterval));
            this.engine.setRefreshInterval(refreshInterval);
        });
    }
}
