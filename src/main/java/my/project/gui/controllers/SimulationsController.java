package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class SimulationsController {
    @FXML
    private ScrollPane simulationBoxLeft;

    @FXML
    private ScrollPane simulationBoxRight;

    @FXML
    private void initialize() {
        // TODO - pass data from input
        IEngine engine = new SimulationEngine();
        Thread engineThread = new Thread((Runnable) engine);

        IMap foldingMap = new FoldingMap(10, 10, .4, 10, 1, 5, 3, 4);
        engine.addData(foldingMap, simulationBoxLeft);

        IMap fencedMap = new FencedMap(10, 10, .4, 1000, 1, 5, 3, 100);
        engine.addData(fencedMap, simulationBoxRight);

        engineThread.start();
    }
}
