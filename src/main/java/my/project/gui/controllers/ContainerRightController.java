package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;

public class ContainerRightController extends AbstractContainerController {
    @FXML
    protected AnchorPane simulationBox;

    @FXML
    protected AnchorPane chartBox;

    @FXML
    private void initialize() {
        this.legendSide = Side.RIGHT;
        setSimulationBox(simulationBox);
        setChartBox(chartBox);
    }
}
