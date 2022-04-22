package my.project.gui.controllers;

import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;
import my.project.gui.charts.ChartDrawer;
import my.project.gui.simulation.visualization.SimulationVisualizer;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;

import my.project.simulation.maps.IMap;
import my.project.simulation.sprites.Animal;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.Vector2D;

import java.util.Set;

public class AbstractContainerController {
    protected Side legendSide;

    private AnchorPane simulationBox;
    private AnchorPane chartBox;

    private IMap map;
    private IEngine engine;
    private SimulationVisualizer simulationVisualizer;

    private MenuController menuBoxController;

    public void setSimulationBox(AnchorPane simulationBox) {
        this.simulationBox = simulationBox;
    }

    public void setChartBox(AnchorPane chartBox) {
        this.chartBox = chartBox;
    }

    public void setMenuBoxController(MenuController menuBoxController) {
        this.menuBoxController = menuBoxController;
    }

    public IEngine getEngine() {
        return engine;
    }

    public IMap getMap() {
        return map;
    }

    public SimulationVisualizer getVisualizer() {
        return simulationVisualizer;
    }

    public void launch(IMap map) {
        this.map = map;
        StatsMeter statsMeter = map.getStatsMeter();
        simulationVisualizer = new SimulationVisualizer(map, simulationBox);
        engine = new SimulationEngine(map, simulationVisualizer);
        Thread engineThread = new Thread((Runnable) engine);
        statsMeter.setChartDrawer(new ChartDrawer(chartBox, null, "Day", "Count", legendSide));
        statsMeter.setDominantGenomesBox(menuBoxController.getDominantGenomesBox());
        statsMeter.setDominantGenomesCountLabel(menuBoxController.getDominantGenomesCountLabel());
        statsMeter.setAllAnimalsCountLabel(menuBoxController.getAllAnimalsCountLabel());
        map.getGridBuilder().setEventsController(this);
        menuBoxController.setupRefreshInterval();
        menuBoxController.updateInitialSettings(map.getSettings());
        engineThread.start();
    }

    public void notifyClick(Vector2D position) {
        menuBoxController.notifyClick(position);
    }

    protected void enableAnimalPicker() {
        Set<Animal> maxEnergyAnimals = map.getMaxEnergyFieldAnimals();
        simulationVisualizer.bringAnimalsToTop(maxEnergyAnimals);
        simulationVisualizer.showAnimalsIDs(map.getMaxEnergyFieldAnimals());
    }

    protected void disableAnimalsPicker() {
        simulationVisualizer.hideAnimalsIDs(map.getMaxEnergyFieldAnimals());
    }

    protected void setAnimalTracker(Animal animal) {
        map.setAnimalTracker(menuBoxController.createAnimalTracker(animal));
    }

    protected void saveStatsFile() {
        map.getStatsMeter().generateCSVFile();
    }
}
