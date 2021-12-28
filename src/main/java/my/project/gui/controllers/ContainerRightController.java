package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public class ContainerRightController extends AbstractContainerController {
    @FXML
    protected AnchorPane simulationBox;

    @FXML
    protected AnchorPane chartBox;

    @FXML
    private Slider refreshSlider;

    @FXML
    private Button pauseButton;

    @FXML
    private Label refreshLabel;

    @FXML
    private void initialize() {
        this.legendSide = Side.RIGHT;
        setSimulationBox(simulationBox);
        setChartBox(chartBox);
        setRefreshSlider(refreshSlider);
        setRefreshLabel(refreshLabel);
    }

    @FXML
    private void onPause() {
        pauseButtonClicked(pauseButton);
    }
}
