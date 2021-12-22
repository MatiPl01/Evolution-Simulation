package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.enums.MapStrategy;
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

        IMap foldingMap = new FoldingMap(30, 30, .75, 2000, 2, 5, 3, 50);
        foldingMap.setStrategy(MapStrategy.MAGIC);
        engine.addData(foldingMap, simulationBoxLeft);

        IMap fencedMap = new FencedMap(20, 20, .1, 5000, 1, 5, 3, 40);
        engine.addData(fencedMap, simulationBoxRight);

        engineThread.start();
    }
}
