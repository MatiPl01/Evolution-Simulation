package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class SimulationsContainerController {
    @FXML
    private ScrollPane simulationBoxLeft;

    @FXML
    private ScrollPane simulationBoxRight;

    @FXML
    private void initialize() {
        // TODO - move this code to the better place (and add a possibility to specify input data)
        IEngine engine = new SimulationEngine();

        IMap foldingMap = new FoldingMap(4, 6, .45, 10, 1, 1, 1, 3);
        engine.addData(foldingMap, simulationBoxLeft);

//        IMap fencedMap = new FencedMap(10, 10, .4, 10, 1, 1, 1, 3);
//        engine.addData(fencedMap, simulationBoxRight);

        engine.run();
    }
}
