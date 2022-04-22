package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import my.project.gui.config.MapSettings;
import my.project.simulation.maps.MapType;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;

import java.io.IOException;
import java.util.Map;

public class MainController {
    private double mainPaneWidth = 0;
    private double mainPaneHeight = 0;
    private static final String LEFT_CONTAINER_PATH = "/fxml/ContainerLeft.fxml";
    private static final String RIGHT_CONTAINER_PATH = "/fxml/ContainerRight.fxml";

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private Label infoBoxLeft;

    @FXML
    private Label infoBoxRight;

    public void init(Map<MapType, MapSettings> settings) throws IllegalArgumentException, IOException {
        if (settings.containsKey(MapType.FOLDING) && settings.containsKey(MapType.FENCED)) {
            loadContainer(createMap(MapType.FOLDING, settings.get(MapType.FOLDING)), LEFT_CONTAINER_PATH, infoBoxLeft);
            loadContainer(createMap(MapType.FENCED, settings.get(MapType.FENCED)), RIGHT_CONTAINER_PATH, infoBoxRight);
        } else if (settings.containsKey(MapType.FOLDING)) {
            loadContainer(createMap(MapType.FOLDING, settings.get(MapType.FOLDING)), LEFT_CONTAINER_PATH, infoBoxLeft);
        } else if (settings.containsKey(MapType.FENCED)) {
            loadContainer(createMap(MapType.FENCED, settings.get(MapType.FENCED)), LEFT_CONTAINER_PATH, infoBoxLeft);
        } else throw new IllegalArgumentException("Invalid settings. Cannot create a map.");
    }

    private IMap createMap(MapType mapType, MapSettings mapSettings) {
        return switch (mapType) {
            case FOLDING -> new FoldingMap(mapSettings);
            case FENCED  -> new FencedMap(mapSettings);
        };
    }

    private void loadContainer(IMap map, String path, Label infoBox) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        SplitPane splitPane = loader.load();
        AbstractContainerController controller = loader.getController();
        mainSplitPane.getItems().add(splitPane);
        mainPaneWidth += splitPane.getPrefWidth();
        mainPaneHeight = Math.max(mainPaneHeight, splitPane.getPrefHeight());
        mainSplitPane.setPrefWidth(mainPaneWidth);
        mainSplitPane.setPrefHeight(mainPaneHeight);
        map.setInfoBox(infoBox);
        controller.launch(map);
    }
}
