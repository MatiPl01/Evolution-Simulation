package my.project.simulation.engine;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import my.project.simulation.maps.IMap;

public interface IEngine {
    void run();

    void addData(IMap map, AnchorPane parentContainer);
}
