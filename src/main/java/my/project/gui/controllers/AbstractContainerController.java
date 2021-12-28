package my.project.gui.controllers;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import my.project.gui.charts.ChartDrawer;
import my.project.gui.enums.TrackingButtonState;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.gui.enums.SimulationState;
import my.project.simulation.maps.AbstractMap;
import my.project.simulation.maps.IMap;
import my.project.simulation.sprites.Animal;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.AnimalTracker;
import my.project.simulation.utils.Vector2D;

import java.util.Set;
import java.util.stream.Collectors;

public class AbstractContainerController {
    private static final int DEFAULT_REFRESH_INTERVAL = 300; // ms
    private static final int MIN_REFRESH_INTERVAL = 1;    // ms
    private static final int MAX_REFRESH_INTERVAL = 1000; // ms

    private Slider refreshSlider;
    private Label refreshLabel;
    protected Side legendSide;
    private Button pauseButton;
    private Button trackButton;
    private Button dominantGenomesButton;
    private AnchorPane simulationBox;
    private AnchorPane chartBox;
    private VBox dominantGenomesBox;
    private VBox trackedAnimalBox;
    private Label trackedAnimalID;
    private Label trackedAnimalChildren;
    private Label trackedAnimalDescendants;
    private Label trackedAnimalDeath;

    private IMap map;
    private IEngine engine;
    private SimulationVisualizer simulationVisualizer;

    private boolean isShowingDominantGenomesAnimals = false;
    private boolean isShowingAnimalsPicker = false;
    private TrackingButtonState trackingButtonState = TrackingButtonState.CHOOSE;

    public void setSimulationBox(AnchorPane simulationBox) {
        this.simulationBox = simulationBox;
    }

    public void setChartBox(AnchorPane chartBox) {
        this.chartBox = chartBox;
    }

    public void setPauseButton(Button pauseButton) {
        this.pauseButton = pauseButton;
    }

    public void setRefreshSlider(Slider refreshSlider) {
        this.refreshSlider = refreshSlider;
    }

    public void setRefreshLabel(Label refreshLabel) {
        this.refreshLabel = refreshLabel;
    }

    public void setDominantGenomesBox(VBox dominantGenomesBox) {
        this.dominantGenomesBox = dominantGenomesBox;
    }

    public void setTrackButton(Button trackButton) {
        this.trackButton = trackButton;
    }

    public void setDominantGenomesButton(Button dominantGenomesButton) {
        this.dominantGenomesButton = dominantGenomesButton;
    }

    public void setTrackedAnimalBoxes(VBox trackedAnimalBox,
                                      Label trackedAnimalID,
                                      Label trackedAnimalChildren,
                                      Label trackedAnimalDescendants,
                                      Label trackedAnimalDeath) {
        this.trackedAnimalBox = trackedAnimalBox;
        this.trackedAnimalID = trackedAnimalID;
        this.trackedAnimalChildren = trackedAnimalChildren;
        this.trackedAnimalDescendants = trackedAnimalDescendants;
        this.trackedAnimalDeath = trackedAnimalDeath;
    }

    public void launch(IMap map) {
        this.map = map;
        StatsMeter statsMeter = map.getStatsMeter();
        statsMeter.setChartDrawer(new ChartDrawer(chartBox, null, "Day", "Count", legendSide));
        statsMeter.setDominantGenomesBox(dominantGenomesBox);
        simulationVisualizer = new SimulationVisualizer(map, simulationBox);
        engine = new SimulationEngine(map, simulationVisualizer);
        Thread engineThread = new Thread((Runnable) engine);
        setupRefreshInterval();
        engineThread.start();
        map.getGridBuilder().setEventsController(this);
    }

    private void setupRefreshInterval() {
        double ratio = 1. * (DEFAULT_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL)
                / (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL);
        refreshSlider.setValue(100. * (1 - ratio));
        refreshLabel.setText(String.valueOf(DEFAULT_REFRESH_INTERVAL));
        engine.setRefreshInterval(DEFAULT_REFRESH_INTERVAL);

        refreshSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int refreshInterval = (int) ((1 - refreshSlider.getValue() / 100)
                    * (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL) + MIN_REFRESH_INTERVAL);
            refreshLabel.setText(String.valueOf(refreshInterval));
            engine.setRefreshInterval(refreshInterval);
        });
    }

    protected void pauseButtonClicked() {
        if (engine.getState() == SimulationState.PAUSED) {
            engine.start();
            pauseButton.setText("Pause");
            dominantGenomesButton.setDisable(true);
            trackButton.setDisable(true);
        } else {
            engine.pause();
            pauseButton.setText("Start");
            dominantGenomesButton.setDisable(false);
            trackButton.setDisable(false);
        }
    }

    protected void dominantButtonClicked() {
        if (isShowingDominantGenomesAnimals) {
            simulationVisualizer.hideDominantGenomesAnimals();
            dominantGenomesButton.setText("Show");
            trackButton.setDisable(false);
            pauseButton.setDisable(false);
        } else {
            simulationVisualizer.showDominantGenomesAnimals();
            dominantGenomesButton.setText("Hide");
            trackButton.setDisable(true);
            pauseButton.setDisable(true);
        }
        isShowingDominantGenomesAnimals = !isShowingDominantGenomesAnimals;
    }

    protected void trackButtonClicked() {
        switch (trackingButtonState) {
            case CHOOSE -> {
                enableAnimalPicker();
                pauseButton.setDisable(true);
                dominantGenomesButton.setDisable(true);
                trackButton.setText("Cancel");
                trackingButtonState = TrackingButtonState.CANCEL;
            }
            case CANCEL -> {
                disableAnimalsPicker();
                pauseButton.setDisable(false);
                dominantGenomesButton.setDisable(false);
                trackButton.setText("Choose");
                trackingButtonState = TrackingButtonState.CHOOSE;
            }
            case STOP -> {
                map.removeAnimalTracker();
                trackButton.setText("Choose");
                trackingButtonState = TrackingButtonState.CHOOSE;
            }
        }
    }

    private void enableAnimalPicker() {
        isShowingAnimalsPicker = true;
        Set<Animal> maxEnergyAnimals = map.getMaxEnergyFieldAnimals();
        simulationVisualizer.bringAnimalsToTop(maxEnergyAnimals);
        simulationVisualizer.showAnimalsIDs(map.getMaxEnergyFieldAnimals());
    }

    private void disableAnimalsPicker() {
        isShowingAnimalsPicker = false;
        simulationVisualizer.hideAnimalsIDs(map.getMaxEnergyFieldAnimals());
    }

    private void setAnimalTracker(Animal animal) {
        map.setAnimalTracker(new AnimalTracker(animal, trackedAnimalID,
                                                       trackedAnimalChildren,
                                                       trackedAnimalDescendants,
                                                       trackedAnimalDeath));
    }

    public void notifyClick(Vector2D position) {
        if (trackingButtonState == TrackingButtonState.CANCEL) {
            Animal animal = map.getMaxEnergyFieldAnimal(position);
            if (animal == null) return;
            setAnimalTracker(animal);
            disableAnimalsPicker();
            pauseButton.setDisable(false);
            dominantGenomesButton.setDisable(false);
            trackingButtonState = TrackingButtonState.STOP;
            trackButton.setText("Stop");
        }
    }

    protected void saveStatsFile() {
        map.getStatsMeter().generateCSVFile();
    }
}
