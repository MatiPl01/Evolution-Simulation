package my.project.gui.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import my.project.simulation.utils.Time;

public class InfoLogger {
    private final Label infoBox;
    private String defaultText;

    public InfoLogger(Label infoBox) {
        this.infoBox = infoBox;
    }

    public void setDefaultText(String text) {
        defaultText = text;
        Platform.runLater(() -> infoBox.setText(text));
    }

    public void displayInfo(String text, int milliseconds) {
        Platform.runLater(() -> infoBox.setText(text));
        Time.setTimeout(() -> Platform.runLater(() -> infoBox.setText(defaultText)), milliseconds);
    }
}
