package my.project.simulation.engine;

import javafx.scene.layout.Pane;
import my.project.simulation.maps.IMap;

public interface IEngine {
    void run();

    void addData(IMap map, Pane parentPane);

    void removeData(IMap map);
}
