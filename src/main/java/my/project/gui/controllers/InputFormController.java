package my.project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import my.project.gui.config.MapConfig;
import my.project.gui.config.MapSettings;
import my.project.gui.config.ValueConfig;
import my.project.simulation.maps.MapType;
import my.project.simulation.enums.MapStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class InputFormController {
    private static final String NORMAL_STRATEGY_NAME = "normal";
    private static final String MAGIC_STRATEGY_NAME = "magic";
    private static final String DEFAULT_STRATEGY_NAME = NORMAL_STRATEGY_NAME;

    private String currentStrategy = NORMAL_STRATEGY_NAME;

    private InputBoxController parentController;
    private MapConfig mapConfig;
    private final List<TextField> textFields = new ArrayList<>();

    @FXML
    private TextField widthInput;

    @FXML
    private TextField heightInput;

    @FXML
    private TextField ratioInput;

    @FXML
    private TextField initialEnergyInput;

    @FXML
    private TextField moveEnergyInput;

    @FXML
    private TextField bushEnergyInput;

    @FXML
    private TextField grassEnergyInput;

    @FXML
    private TextField initialAnimalsInput;

    @FXML
    private ComboBox<String> strategyCombobox;

    @FXML
    private TextField magicRespawnsInput;

    @FXML
    private TextField magicAnimalsInput;

    @FXML
    private CheckBox dontShowCheckbox;

    @FXML
    private void initialize() {
        setupCombobox(NORMAL_STRATEGY_NAME, MAGIC_STRATEGY_NAME);
        textFields.add(widthInput);
        textFields.add(heightInput);
        textFields.add(ratioInput);
        textFields.add(initialEnergyInput);
        textFields.add(moveEnergyInput);
        textFields.add(bushEnergyInput);
        textFields.add(grassEnergyInput);
        textFields.add(initialAnimalsInput);
        textFields.add(magicRespawnsInput);
        textFields.add(magicAnimalsInput);
    }

    @FXML
    private void onStrategyChange() {
        currentStrategy = strategyCombobox.getValue();
        if (Objects.equals(currentStrategy, NORMAL_STRATEGY_NAME)) disableMagicInput();
        else enableMagicInput();
    }

    @FXML
    private void onDontShowChecked() {
        parentController.dontShowChecked(dontShowCheckbox);
        if (dontShowCheckbox.isSelected()) disableInputFields();
        else enableInputFields();
    }

    public void setData(MapType mapType, InputBoxController parentController, MapConfig mapConfig) {
        this.parentController = parentController;
        this.mapConfig = mapConfig;
        parentController.setCheckBox(mapType, dontShowCheckbox);
        fillWithDefaults();
        setupInputValidators();
    }

    public void fillWithDefaults() {
        widthInput.setText(String.valueOf(mapConfig.width.def));
        heightInput.setText(String.valueOf(mapConfig.height.def));
        ratioInput.setText(String.valueOf(mapConfig.jungleRatio.def));
        initialEnergyInput.setText(String.valueOf(mapConfig.startEnergy.def));
        moveEnergyInput.setText(String.valueOf(mapConfig.moveEnergy.def));
        bushEnergyInput.setText(String.valueOf(mapConfig.bushEnergy.def));
        grassEnergyInput.setText(String.valueOf(mapConfig.grassEnergy.def));
        initialAnimalsInput.setText(String.valueOf(mapConfig.animalsCount.def));
        strategyCombobox.setValue(DEFAULT_STRATEGY_NAME);
        if (Objects.equals(currentStrategy, NORMAL_STRATEGY_NAME)) disableMagicInput();
        else enableMagicInput();
    }

    public void clearInput() {
        textFields.forEach(field -> field.setText(""));
        strategyCombobox.setValue(DEFAULT_STRATEGY_NAME);
    }

    private void disableInputFields() {
        textFields.forEach(field -> field.setDisable(true));
        strategyCombobox.setDisable(true);
    }

    private void enableInputFields() {
        textFields.forEach(field -> field.setDisable(false));
        strategyCombobox.setDisable(false);
    }

    public void setupCombobox(String ...values) {
        strategyCombobox.getItems().addAll(values);
        strategyCombobox.setPromptText(currentStrategy);
    }

    public MapSettings generateMapSettings() {
        return new MapSettings(
            parseInt(widthInput.getText()),
            parseInt(heightInput.getText()),
            Double.parseDouble(ratioInput.getText()),
            parseInt(initialEnergyInput.getText()),
            parseInt(moveEnergyInput.getText()),
            parseInt(bushEnergyInput.getText()),
            parseInt(grassEnergyInput.getText()),
            parseInt(initialAnimalsInput.getText()),
            parseInt(magicRespawnsInput.getText()),
            parseInt(magicAnimalsInput.getText()),
            Objects.equals(strategyCombobox.getValue(), NORMAL_STRATEGY_NAME) ? MapStrategy.NORMAL : MapStrategy.MAGIC
        );
    }

    public boolean checkIfValid() {
        for (TextField textField: textFields) {
            if (Objects.equals(textField.getText(), "")) return false;
        }
        return true;
    }

    private void disableMagicInput() {
        magicRespawnsInput.setText("0");
        magicAnimalsInput.setText("0");
        magicRespawnsInput.setDisable(true);
        magicAnimalsInput.setDisable(true);
    }

    private void enableMagicInput() {
        magicRespawnsInput.setText(String.valueOf(mapConfig.magicRespawnsCount.def));
        magicAnimalsInput.setText(String.valueOf(mapConfig.magicRespawnAnimals.def));
        magicRespawnsInput.setDisable(false);
        magicAnimalsInput.setDisable(false);
    }

    private void setupInputValidators() {
        setupIntValueValidator(widthInput, mapConfig.width);
        setupIntValueValidator(heightInput, mapConfig.height);
        setupIntValueValidator(initialEnergyInput, mapConfig.startEnergy);
        setupIntValueValidator(moveEnergyInput, mapConfig.moveEnergy);
        setupIntValueValidator(bushEnergyInput, mapConfig.bushEnergy);
        setupIntValueValidator(grassEnergyInput, mapConfig.grassEnergy);
        setupIntValueValidator(initialAnimalsInput, mapConfig.animalsCount);
        setupIntValueValidator(magicRespawnsInput, mapConfig.magicRespawnsCount);
        setupIntValueValidator(magicAnimalsInput, mapConfig.magicRespawnAnimals);
        setupDoubleValueValidator(ratioInput, mapConfig.jungleRatio);
    }

    private int validateInput(int value, ValueConfig<Integer> valueConfig) {
        if (value < valueConfig.min) return valueConfig.min;
        if (value > valueConfig.max) return valueConfig.max;
        return value;
    }

    private double validateInput(double value, ValueConfig<Double> valueConfig) {
        if (value < valueConfig.min) return valueConfig.min;
        if (value > valueConfig.max) return valueConfig.max;
        return value;
    }

    private void setupIntValueValidator(TextField textField, ValueConfig<Integer> valueConfig) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, "")) return;
            try {
                textField.setText(String.valueOf(validateInput(parseInt(newValue), valueConfig)));
            } catch (NumberFormatException ignored) {
                textField.setText(oldValue);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String inputText = textField.getText();
                if (Objects.equals(inputText, "")) inputText = String.valueOf(valueConfig.def);
                else inputText = String.valueOf(validateInput(parseInt(inputText), valueConfig));
                textField.setText(inputText);
            }
        });
    }

    private void setupDoubleValueValidator(TextField textField, ValueConfig<Double> valueConfig) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, "")) return;
            try {
                double input = Double.parseDouble(newValue);
                double result = validateInput(input, valueConfig);
                if (result != input) textField.setText(String.valueOf(result));
            } catch (NumberFormatException ignored) {
                textField.setText(oldValue);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String inputText = textField.getText();
                if (Objects.equals(inputText, "")) inputText = String.valueOf(valueConfig.def);
                else inputText = String.valueOf(validateInput(Double.parseDouble(inputText), valueConfig));
                textField.setText(inputText);
            }
        });
    }
}
