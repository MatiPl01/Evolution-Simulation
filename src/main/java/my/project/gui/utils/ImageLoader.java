package my.project.gui.utils;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageLoader {
    public static Image loadImage(String imagePath) {
        try {
            return new Image(new FileInputStream(imagePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
