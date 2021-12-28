package my.project.simulation.maps;

import javafx.scene.control.Label;
import my.project.gui.simulation.grid.IBuilder;
import my.project.simulation.enums.MapArea;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.sprites.Animal;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.AnimalTracker;
import my.project.simulation.utils.Vector2D;

import java.util.List;
import java.util.Set;

public interface IMap {
    int getHeight();

    int getWidth();

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

    AnimalTracker getAnimalTracker();

    long getCurrentDayNum();

    Set<List<Integer>> getDominantGenomes();

    Set<Animal> getAllAnimals();

    Set<Animal> getAnimalsWithGenome(List<Integer> genome);

    Animal getMaxEnergyFieldAnimal(Vector2D position);

    Set<Animal> getAllFieldAnimals(Vector2D position);

    Set<Animal> getMaxEnergyFieldAnimals();

    Set<Animal> getDominantGenomesAnimals();

    void setInfoBox(Label infoBox);
}
