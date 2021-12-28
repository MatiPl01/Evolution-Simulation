package my.project.gui.controllers;

import javafx.fxml.FXML;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

public class MainController {
    @FXML
    private ContainerLeftController containerLeftController;

    @FXML
    private ContainerRightController containerRightController;

    @FXML
    private void initialize() {
        System.out.println("IN INIT");
//        IMap foldingMap = new FoldingMap(30, 20, .5, 100,
//        5, 500, 50, 50);
//        foldingMap.setStrategy(MapStrategy.MAGIC);

        IMap foldingMap = new FoldingMap(2, 2, 1, 5, 5, 5,
                3, 2);
        foldingMap.setStrategy(MapStrategy.MAGIC);

        IMap fencedMap = new FencedMap(10, 10, .5, 100,
                5, 50, 50, 50);

        System.out.println("IN LAUNCH");
        containerLeftController.launch(foldingMap);
        containerRightController.launch(fencedMap);
    }
}
