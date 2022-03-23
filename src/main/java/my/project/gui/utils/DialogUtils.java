package my.project.gui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class DialogUtils {
    public static void informationDialog(String title, String header, String content) {
        Alert informationDialog = new Alert(Alert.AlertType.INFORMATION);
        informationDialog.setTitle(title);
        informationDialog.setHeaderText(header);
        informationDialog.setContentText(content);
        informationDialog.showAndWait();
    }

    public static Optional<ButtonType> confirmationDialog(String title, String header) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(title);
        confirmationDialog.setHeaderText(header);
        return confirmationDialog.showAndWait();
    }

    public static void errorDialog(String title, String header) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle(title);
        errorDialog.setHeaderText(header);
        errorDialog.showAndWait();
    }

    public static String textInputDialog(String title, String header, String content, String defaultValue) {
        TextInputDialog textInputDialog = new TextInputDialog(defaultValue);
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(header);
        textInputDialog.setContentText(content);
        Optional<String> result = textInputDialog.showAndWait();
        return result.orElse(null);
    }
}
