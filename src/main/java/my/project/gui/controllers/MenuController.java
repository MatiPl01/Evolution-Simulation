package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my.project.gui.config.MapSettings;
import my.project.gui.enums.SimulationState;
import my.project.gui.enums.TrackingButtonState;
import my.project.simulation.sprites.Animal;
import my.project.simulation.utils.AnimalTracker;
import my.project.simulation.utils.Converter;
import my.project.simulation.utils.Vector2D;

import java.util.Iterator;
import java.util.Set;

public class MenuController {
    private static final int DEFAULT_REFRESH_INTERVAL = 300; // ms
    private static final int MIN_REFRESH_INTERVAL = 1;       // ms
    private static final int MAX_REFRESH_INTERVAL = 1000;    // ms

    AbstractContainerController containerController;

    private boolean isShowingDominantGenomesAnimals = false;
    private TrackingButtonState trackingButtonState = TrackingButtonState.CHOOSE;

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
    private Label widthLabel;

    @FXML
    private Label heightLabel;

    @FXML
    private Label jungleRatioLabel;

    @FXML
    private Label startEnergyLabel;

    @FXML
    private Label moveEnergyLabel;

    @FXML
    private Label bushEnergyLabel;

    @FXML
    private Label grassEnergyLabel;

    @FXML
    private Label initialAnimalsLabel;

    @FXML
    private Label strategyLabel;

    @FXML
    private Label magicRespawnsLabel;

    @FXML
    private Label magicAnimalsLabel;

    @FXML
    private Label trackedAnimalIDLabel;

    @FXML
    private Label trackedAnimalChildrenLabel;

    @FXML
    private Label trackedAnimalDescendantsLabel;

    @FXML
    private Label trackedAnimalDeathLabel;

    @FXML
    private VBox clickedFieldAnimalsBox;

    @FXML
    private Label clickedFieldPositionLabel;

    @FXML
    private Label dominantGenomesAnimalsCountLabel;

    @FXML
    private Label allAnimalsCountLabel;

    @FXML
    private VBox dominantGenomesBox;

    @FXML
    private void onPause() {
        if (containerController.getEngine().getState() == SimulationState.PAUSED) {
            containerController.getEngine().start();
            pauseButton.setText("Pause");
            dominantGenomesButton.setDisable(true);
            trackButton.setDisable(true);
        } else {
            containerController.getEngine().pause();
            pauseButton.setText("Start");
            dominantGenomesButton.setDisable(false);
            trackButton.setDisable(false);
        }
    }

    @FXML
    private void onDominantShow() {
        if (isShowingDominantGenomesAnimals) {
            containerController.getVisualizer().hideDominantGenomesAnimals();
            dominantGenomesButton.setText("Show");
            trackButton.setDisable(false);
            pauseButton.setDisable(false);
        } else {
            containerController.getVisualizer().showDominantGenomesAnimals();
            dominantGenomesButton.setText("Hide");
            trackButton.setDisable(true);
            pauseButton.setDisable(true);
        }
        isShowingDominantGenomesAnimals = !isShowingDominantGenomesAnimals;
    }

    @FXML
    private void onTrackChoose() {
        switch (trackingButtonState) {
            case CHOOSE -> {
                containerController.enableAnimalPicker();
                pauseButton.setDisable(true);
                dominantGenomesButton.setDisable(true);
                trackButton.setText("Cancel");
                trackingButtonState = TrackingButtonState.CANCEL;
            }
            case CANCEL -> {
                containerController.disableAnimalsPicker();
                pauseButton.setDisable(false);
                dominantGenomesButton.setDisable(false);
                trackButton.setText("Choose");
                trackingButtonState = TrackingButtonState.CHOOSE;
            }
            case STOP -> {
                containerController.getMap().removeAnimalTracker();
                trackButton.setText("Choose");
                trackingButtonState = TrackingButtonState.CHOOSE;
            }
        }
    }

    @FXML
    private void onStatsFileSave() {
        containerController.saveStatsFile();
    }

    protected VBox getDominantGenomesBox() {
        return dominantGenomesBox;
    }

    protected Label getDominantGenomesCountLabel() {
        return dominantGenomesAnimalsCountLabel;
    }

    protected Label getAllAnimalsCountLabel() {
        return allAnimalsCountLabel;
    }

    protected void setContainerController(AbstractContainerController containerController) {
        this.containerController = containerController;
    }

    protected AnimalTracker createAnimalTracker(Animal animal) {
        return new AnimalTracker(animal, trackedAnimalIDLabel,
                                         trackedAnimalChildrenLabel,
                                         trackedAnimalDescendantsLabel,
                                         trackedAnimalDeathLabel);
    }

    protected void notifyClick(Vector2D position) {
        if (trackingButtonState == TrackingButtonState.CANCEL) {
            Animal animal = containerController.getMap().getMaxEnergyFieldAnimal(position);
            if (animal == null) return;
            containerController.setAnimalTracker(animal);
            containerController.disableAnimalsPicker();
            pauseButton.setDisable(false);
            dominantGenomesButton.setDisable(false);
            trackingButtonState = TrackingButtonState.STOP;
            trackButton.setText("Stop");
        }
        updateClickedFieldAnimalsInfo(position);
    }

    protected void setupRefreshInterval() {
        double ratio = 1. * (DEFAULT_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL)
                / (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL);
        refreshSlider.setValue(100. * (1 - ratio));
        refreshLabel.setText(String.valueOf(DEFAULT_REFRESH_INTERVAL));
        containerController.getEngine().setRefreshInterval(DEFAULT_REFRESH_INTERVAL);

        refreshSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int refreshInterval = (int) ((1 - refreshSlider.getValue() / 100)
                    * (MAX_REFRESH_INTERVAL - MIN_REFRESH_INTERVAL) + MIN_REFRESH_INTERVAL);
            refreshLabel.setText(String.valueOf(refreshInterval));
            containerController.getEngine().setRefreshInterval(refreshInterval);
        });
    }

    protected void updateInitialSettings(MapSettings settings) {
        widthLabel.setText(String.valueOf(settings.width()));
        heightLabel.setText(String.valueOf(settings.height()));
        jungleRatioLabel.setText(String.valueOf(settings.jungleRatio()));
        startEnergyLabel.setText(String.valueOf(settings.startEnergy()));
        moveEnergyLabel.setText(String.valueOf(settings.moveEnergy()));
        bushEnergyLabel.setText(String.valueOf(settings.bushEnergy()));
        grassEnergyLabel.setText(String.valueOf(settings.grassEnergy()));
        initialAnimalsLabel.setText(String.valueOf(settings.animalsCount()));
        strategyLabel.setText(String.valueOf(settings.mapStrategy()));
        magicRespawnsLabel.setText(String.valueOf(settings.magicRespawnsCount()));
        magicAnimalsLabel.setText(String.valueOf(settings.magicRespawnAnimals()));
    }

    private void updateClickedFieldAnimalsInfo(Vector2D position) {
        Set<Animal> allFieldAnimals = containerController.getMap().getAllFieldAnimals(position);
        if (allFieldAnimals.size() == 0) return;
        Iterator<Animal> it = allFieldAnimals.iterator();
        clickedFieldPositionLabel.setText(it.next().getDisplayedPosition().toString());
        clickedFieldAnimalsBox.getChildren().clear();
        allFieldAnimals.forEach(animal -> {
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            Label genomeLabel = new Label(Converter.genomeToString(animal.getGenome()));
            Label idLabel = new Label(String.valueOf(animal.getID()));
            hBox.getChildren().addAll(genomeLabel, idLabel);
            clickedFieldAnimalsBox.getChildren().add(hBox);
        });
    }
}
