package my.project.gui;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.Objects;

public class App extends Application {
    private static final String windowTitle = "Evolution Simulation";
    private static final String iconPath = "src/main/resources/images/icon.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainBox.fxml"));
        BorderPane mainContainer = loader.load();
        Scene scene = new Scene(mainContainer);
        String css = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        // Set the icon and the window title
        Image icon = new Image(new FileInputStream(iconPath));
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle(windowTitle);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void init(String[] args) {
        Application.launch(App.class, args);
    }
}
