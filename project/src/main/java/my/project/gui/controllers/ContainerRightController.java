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
