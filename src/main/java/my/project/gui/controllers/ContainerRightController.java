package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ContainerRightController extends AbstractContainerController {
    @FXML
    protected AnchorPane simulationBox;

    @FXML
    protected AnchorPane chartBox;

    @FXML
    private VBox dominantGenomesBox;

    @FXML
    private Slider refreshSlider;

    @FXML
    private Button pauseButton;

    @FXML
    private Label refreshLabel;

    @FXML
    private Button dominantGenomesButton;

    @FXML
    private Button trackButton;

    @FXML
    private VBox trackedAnimalBox;

    @FXML
    private Label trackedAnimalID;

    @FXML
    private Label trackedAnimalChildren;

    @FXML
    private Label trackedAnimalDescendants;

    @FXML
    private Label trackedAnimalDeath;

    @FXML
    private void initialize() {
        this.legendSide = Side.RIGHT;
        setSimulationBox(simulationBox);
        setChartBox(chartBox);
        setPauseButton(pauseButton);
        setRefreshSlider(refreshSlider);
        setRefreshLabel(refreshLabel);
        setDominantGenomesBox(dominantGenomesBox);
        setDominantGenomesButton(dominantGenomesButton);
        setTrackButton(trackButton);
        setTrackedAnimalBoxes(trackedAnimalBox,
                              trackedAnimalID,
                              trackedAnimalChildren,
                              trackedAnimalDescendants,
                              trackedAnimalDeath);
    }

    @FXML
    private void onPause() {
        pauseButtonClicked();
    }

    @FXML
    private void onDominantShow() {
        dominantButtonClicked();
    }

    @FXML
    private void onTrackChoose() {
        trackButtonClicked();
    }

    @FXML
    private void onStatsFileSave() {
        saveStatsFile();
    }
}
