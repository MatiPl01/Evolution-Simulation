package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class SimulationsController {
    @FXML
    private AnchorPane simulationBoxLeft;

    @FXML
    private AnchorPane simulationBoxRight;

    @FXML
    private void initialize() {
        // TODO - pass data from input
        IEngine engine = new SimulationEngine();
        Thread engineThread = new Thread((Runnable) engine);

        IMap foldingMap = new FoldingMap(75, 75, .5, 10000,
                1, 6, 3, 10);
        foldingMap.setStrategy(MapStrategy.MAGIC);
        engine.addData(foldingMap, simulationBoxLeft);

        IMap fencedMap = new FencedMap(50, 50, .1, 5000, 1, 5,
                3, 50);
        engine.addData(fencedMap, simulationBoxRight);

        engineThread.start();
    }
}
