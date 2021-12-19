package my.project.simulation.maps;

import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.Vector2D;

import java.util.List;

public interface IMap {
    void addSprite(ISprite sprite);

    void removeSprite(ISprite sprite);

    void changeSpritePosition(ISprite sprite);

    MapArea getAreaType(Vector2D position);

    Vector2D getNextPosition(Vector2D currPosition, Vector2D moveVector);

    void spawnPlants();

    void update();

    int getMoveEnergy();

    int getStartEnergy();

    int getMinBreedEnergy();

    List<Vector2D> getMapBoundingRect();
}
