package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import my.project.gui.config.Config;
import my.project.gui.config.ConfigLoader;
import my.project.gui.config.MapSettings;
import my.project.gui.enums.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class InputBoxController {
    private static final String CONFIG_JSON_PATH = "./src/main/resources/config.json";

    private static final int FOLDING_MAP_TAB_INDEX = 0;
    private static final int FENCED_MAP_TAB_INDEX = 1;
    private static Config config;

    private CheckBox foldingMapCheckbox;
    private CheckBox fencedMapCheckbox;

    @FXML
    private TabPane formTabPane;

    @FXML
    private VBox foldingInputForm;

    @FXML
    private VBox fencedInputForm;

    @FXML
    private InputFormController foldingInputFormController;

    @FXML
    private InputFormController fencedInputFormController;

    @FXML
    private void onStart() {
        Map<MapType, MapSettings> settings = new HashMap<>();
        if (!foldingMapCheckbox.isSelected()) {
            settings.put(MapType.FOLDING, foldingInputFormController.generateMapSettings());
        }
        if (!fencedMapCheckbox.isSelected()) {
            settings.put(MapType.FENCED, fencedInputFormController.generateMapSettings());
        }
        System.out.println(settings);
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
            System.out.println("Is selected");
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
}
