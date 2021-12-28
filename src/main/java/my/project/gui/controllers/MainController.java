package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

import java.io.IOException;

public class MainController {
    private double mainPaneWidth = 0;
    private double mainPaneHeight = 0;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private void initialize() throws IOException {
        IMap foldingMap = new FoldingMap(20, 20, .3, 200, 3, 60,
                40, 40);
        foldingMap.setStrategy(MapStrategy.MAGIC);

        IMap fencedMap = new FencedMap(10, 10, .5, 100,
        5, 50, 50, 50);

        loadContainer(foldingMap, "/fxml/ContainerLeft.fxml");
        loadContainer(fencedMap, "/fxml/ContainerRight.fxml");
    }

    private void loadContainer(IMap map, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        SplitPane splitPane = loader.load();
        AbstractContainerController controller = loader.getController();
        mainSplitPane.getItems().add(splitPane);
        mainPaneWidth += splitPane.getPrefWidth();
        mainPaneHeight = Math.max(mainPaneHeight, splitPane.getPrefHeight());
        System.out.println(mainPaneWidth);
        System.out.println(mainPaneHeight);
        mainSplitPane.setPrefWidth(mainPaneWidth);
        mainSplitPane.setPrefHeight(mainPaneHeight);
        controller.launch(map);
    }
}
