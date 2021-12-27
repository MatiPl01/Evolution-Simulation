package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;

public class ContainerLeftController extends AbstractContainerController {
    @FXML
    private AnchorPane simulationBox;

    @FXML
    private AnchorPane chartBox;

    @FXML
    private void initialize() {
        this.legendSide = Side.LEFT;
        setSimulationBox(simulationBox);
        setChartBox(chartBox);
    }
}
