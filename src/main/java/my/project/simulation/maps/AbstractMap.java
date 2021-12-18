package my.project.simulation.maps;

import my.project.simulation.IObserver;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.Animal;
import my.project.simulation.sprites.Grass;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.EnergyComparator;
import my.project.simulation.utils.Vector2D;

import java.util.*;

public abstract class AbstractMap implements IMap, IObserver {
    private final int totalWidth;
    private final int totalHeight;
    private final int jungleWidth;
    private final int jungleHeight;

    protected final Map<Vector2D, SortedSet<Animal>> mapAnimals = new HashMap<>();
    protected final Map<Vector2D, Grass> mapGrass = new HashMap<>();

    AbstractMap(int totalWidth, int totalHeight, int jungleWidth, int jungleHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.jungleWidth = jungleWidth;
        this.jungleHeight = jungleHeight;
    }

    @Override
    public void changeSpritePosition(ISprite sprite) throws IllegalArgumentException, NoSuchElementException {
        if (!(sprite instanceof Animal)) {
            throw new IllegalArgumentException("Cannot change position of a sprite which is not an animal");
        } else {
            Vector2D prevPosition = ((Animal) sprite).getPrevPosition();
            SortedSet<Animal> animals = mapAnimals.get(prevPosition);
            if (animals == null || !animals.remove(sprite)) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
            addSprite(sprite);
        }
    }

    @Override
    public void removeSprite(ISprite sprite) throws NoSuchElementException {
        Vector2D position;
        // If a sprite is an animal object
        if (sprite instanceof Animal) {
            position = ((Animal) sprite).getCurrPosition();
            SortedSet<Animal> animals = mapAnimals.get(position);
            if (animals == null || !animals.remove(sprite)) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
        // If a sprite is a grass object
        } else if (sprite instanceof Grass) {
            position = ((Grass) sprite).getPosition();
            if (mapGrass.remove(position) == null) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
        }
    }

    @Override
    public void addSprite(ISprite sprite) throws IllegalArgumentException {
        Vector2D position;
        // If a sprite is an animal object
        if (sprite instanceof Animal) {
            position = ((Animal) sprite).getCurrPosition();
            // Create the new animals list if there is no animals list on the specified position
            if (mapAnimals.get(position) == null) {
                mapAnimals.put(position, new TreeSet<>(new EnergyComparator()));
            }
            // Add an animal to the list
            mapAnimals.get(position).add((Animal)sprite);
        // If a sprite is a grass object
        } else if (sprite instanceof Grass) {
            position = ((Grass) sprite).getPosition();
            // Place grass on a field only if there is no other element occupying this field
            if (mapGrass.get(position) != null || mapAnimals.get(position) != null) {
                String message = "Cannot place grass on field: " + position + ". Field is not empty.";
                throw new IllegalArgumentException(message);
            }
            mapGrass.put(position, (Grass)sprite);
        }
    }

    @Override
    public MapArea getAreaType(Vector2D position) {
        int x = position.getX();
        int y = position.getY();
        if (Math.abs(x) < (jungleWidth + 1) / 2 && Math.abs(y) < (jungleHeight + 1) / 2) {
            return MapArea.JUNGLE;
        }
        return MapArea.STEPPE;
    }
}
