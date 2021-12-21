package my.project.simulation.maps;

import my.project.gui.simulation.grid.IBuilder;
import my.project.simulation.enums.MapArea;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.AnimalTracker;
import my.project.simulation.utils.Vector2D;

import java.util.List;

public interface IMap {
    MapArea getAreaType(Vector2D position);

    Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector);

    void spawnPlants();

    void update();

    int getMoveEnergy();

    int getStartEnergy();

    int getMinBreedEnergy();

    List<Vector2D> getMapBoundingRect();

    void initialize();

    void setGridBuilder(IBuilder gridBuilder);

    long getNewAnimalID();

    IBuilder getGridBuilder();

    boolean areAnimalsAlive();

    StatsMeter getStatsMeter();

    void setAnimalTracker(AnimalTracker tracker);

    void removeAnimalTracker();

    long getCurrentDayNum();
}
