package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ContainerLeftController extends AbstractContainerController {
    @FXML
    protected AnchorPane simulationBox;

    @FXML
    protected AnchorPane chartBox;

    @FXML
    protected MenuController menuBoxController;

    @FXML
    private void initialize() {
        this.legendSide = Side.LEFT;
        setChartBox(chartBox);
        setSimulationBox(simulationBox);
        setMenuBoxController(menuBoxController);
        menuBoxController.setContainerController(this);
    }
}
