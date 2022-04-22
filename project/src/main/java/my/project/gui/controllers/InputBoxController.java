package my.project.gui.controllers;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import my.project.gui.config.Config;
import my.project.gui.config.ConfigLoader;
import my.project.gui.config.MapSettings;
import my.project.gui.utils.DialogUtils;
import my.project.simulation.maps.MapType;

import java.io.IOException;
import java.util.*;

public class InputBoxController {
    private static final String CONFIG_JSON_PATH = "./src/main/resources/config.json";
    private static final String MAIN_SCENE_PATH = "/fxml/MainBox.fxml";

    private CheckBox foldingMapCheckbox;
    private CheckBox fencedMapCheckbox;

    @FXML
    private TabPane formTabPane;

    @FXML
    private InputFormController foldingInputFormController;

    @FXML
    private InputFormController fencedInputFormController;

    @FXML
    private void onStart() {
        Map<MapType, MapSettings> settings = new HashMap<>();
        if (!foldingMapCheckbox.isSelected()) {
            if (!foldingInputFormController.checkIfValid()) {
                DialogUtils.informationDialog("Invalid settings",
                        "Invalid settings in Folding map form" , "Found empty text fields");
                return;
            }
            settings.put(MapType.FOLDING, foldingInputFormController.generateMapSettings());
        }
        if (!fencedMapCheckbox.isSelected()) {
            if (!fencedInputFormController.checkIfValid()) {
                DialogUtils.informationDialog("Invalid settings",
                        "Invalid settings in Fenced map form" , "Found empty text fields");
                return;
            }
            settings.put(MapType.FENCED, fencedInputFormController.generateMapSettings());
        }
        boolean areValid = validateSettings(settings);
        if (areValid) openMainScene(settings);
    }

    @FXML
    private void onClear() {
        switch (formTabPane.getSelectionModel().getSelectedIndex()) {
            case 0 -> foldingInputFormController.clearInput();
            case 1 -> fencedInputFormController.clearInput();
        }
        uncheckCheckboxes();
    }

    @FXML
    private void onReset() {
        switch (formTabPane.getSelectionModel().getSelectedIndex()) {
            case 0 -> foldingInputFormController.fillWithDefaults();
            case 1 -> fencedInputFormController.fillWithDefaults();
        }
        uncheckCheckboxes();
    }

    @FXML
    private void initialize() {
        Config config = loadConfig();
        foldingInputFormController.setData(MapType.FOLDING, this, config.foldingMap);
        fencedInputFormController.setData(MapType.FENCED,this, config.fencedMap);
    }

    public void setCheckBox(MapType mapType, CheckBox checkBox) {
        switch (mapType) {
            case FOLDING -> foldingMapCheckbox = checkBox;
            case FENCED  -> fencedMapCheckbox = checkBox;
        }
    }

    public void dontShowChecked(CheckBox checkBox) {
        if (checkBox.isSelected()) {
            if (checkBox == foldingMapCheckbox) fencedMapCheckbox.setDisable(true);
            else foldingMapCheckbox.setDisable(true);
        } else uncheckCheckboxes();
    }

    private void uncheckCheckboxes() {
        fencedMapCheckbox.setDisable(false);
        foldingMapCheckbox.setDisable(false);
    }

    private Config loadConfig() {
        try {
            return ConfigLoader.load(CONFIG_JSON_PATH);
        } catch (IOException e) {
            System.out.println("Failed to load config file. Exiting.");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void openMainScene(Map<MapType, MapSettings> settings) {
        // Load main container
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(MAIN_SCENE_PATH));
        BorderPane borderPane = null;
        try {
            borderPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainController mainController = loader.getController();

        // Load maps containers
        try {
            mainController.init(settings);
        } catch (IOException e) {
            System.out.println("Error while loading map containers");
            e.printStackTrace();
            return;
        }

        // Set main container in the scene
        Stage stage = (Stage)formTabPane.getScene().getWindow();
        assert borderPane != null;
        Scene scene = new Scene(borderPane);
        String css = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    private boolean validateSettings(Map<MapType, MapSettings> settings) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<MapType> invalidSet = new HashSet<>();

        for (MapType mapType: settings.keySet()) {
            MapSettings s = settings.get(mapType);
            int fieldsCount = s.width() * s.height();
            stringBuilder.append("\nIn ").append(mapType).append(" settings:\n");
            if (s.animalsCount() > fieldsCount) {
                stringBuilder.append("Number of animals should be lower than a number of map fields.\n");
                invalidSet.add(mapType);
            }
            if (s.animalsCount() <= s.magicRespawnAnimals()) {
                stringBuilder.append("Number of animals for a magic respawn should be lower than an initial number of animals.\n");
                invalidSet.add(mapType);
            }
            if (s.magicRespawnAnimals() > fieldsCount / 2) {
                stringBuilder.append("Number of animals for a magic respawn should be lower than half the number of fields.\n");
                invalidSet.add(mapType);
            }
            if (!invalidSet.contains(mapType)) {
                stringBuilder.append("No issues were found.\n");
            }
        }

        if (invalidSet.size() > 0) {
            List<String> invalidList = new ArrayList<>();
            for (MapType mapType: invalidSet) invalidList.add(mapType.toString());
            DialogUtils.informationDialog("Invalid settings", "Invalid settings were found in " + String.join(", ", invalidList), stringBuilder.toString());
            return false;
        }

        return true;
    }
}
