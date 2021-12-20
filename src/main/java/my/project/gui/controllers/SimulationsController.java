package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class SimulationsController {
    private final IEngine engine = new SimulationEngine(this);

    @FXML
    private ScrollPane simulationBoxLeft;

    @FXML
    private ScrollPane simulationBoxRight;

    @FXML
    private void initialize() {
        // TODO - pass data from input
        Thread engineThread = new Thread((Runnable) engine);

        IMap foldingMap = new FoldingMap(10, 10, .2, 2000, 1, 1, 1, 100);
        engine.addData(foldingMap, simulationBoxLeft);

        IMap fencedMap = new FencedMap(10, 10, .4, 1000, 3, 5, 3, 100);
        engine.addData(fencedMap, simulationBoxRight);

        engineThread.start();
    }

    public void requestNewFrame() {
        engine.renderNewFrame();
    }
}
