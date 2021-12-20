package my.project.simulation.engine;

import javafx.scene.control.ScrollPane;
import my.project.simulation.maps.IMap;

public interface IEngine {
    void run();

    void addData(IMap map, ScrollPane parentContainer);

    void renderNewFrame();
}
