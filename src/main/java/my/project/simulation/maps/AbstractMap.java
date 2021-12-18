package my.project.simulation.maps;

import my.project.simulation.IObserver;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.*;
import my.project.simulation.utils.EnergyComparator;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.*;

public abstract class AbstractMap implements IMap, IObserver {
    protected final Vector2D mapLowerLeft = new Vector2D(0, 0);
    protected final Vector2D mapUpperRight;
    protected final Vector2D jungleLowerleft;
    protected final Vector2D jungleUpperRight;
    private final int bushEnergy;
    private final int grassEnergy;

    protected final Map<Vector2D, SortedSet<Animal>> mapAnimals = new HashMap<>();
    protected final Map<Vector2D, AbstractPlant> mapPlants = new HashMap<>();

    protected final List<AbstractPlant> eatenPlants = new ArrayList<>();

    AbstractMap(int width, int height, double jungleRatio, int bushEnergy, int grassEnergy) {
        this.bushEnergy = bushEnergy;
        this.grassEnergy = grassEnergy;
        this.mapUpperRight = new Vector2D(width, height);
        int jungleWidth  = (int) (2 * Math.round((width / 2) * jungleRatio) + (width % 2 == 1 ? 1 : 0));
        int jungleHeight = (int) (2 * Math.round((height / 2) * jungleRatio) + (height % 2 == 1 ? 1 : 0));
        this.jungleLowerleft = new Vector2D((width - jungleWidth) / 2, (height - jungleHeight) / 2);
        this.jungleUpperRight = this.jungleLowerleft.add(new Vector2D(jungleWidth, jungleHeight));
    }

    @Override
    public void changeSpritePosition(ISprite sprite) throws IllegalArgumentException, NoSuchElementException {
        if (!(sprite instanceof Animal animal)) {
            throw new IllegalArgumentException("Cannot change position of a sprite which is not an animal");
        } else {
            // Remove an animal from the previous position on a map
            removeAnimal(animal, animal.getPrevPosition());
            // Add an animal at a new position on the map
            addSprite(sprite);
        }
    }

    @Override
    public void removeSprite(ISprite sprite) throws NoSuchElementException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) removeAnimal(animal, animal.getCurrPosition());
        // If a sprite is a plant object
        else if (sprite instanceof AbstractPlant) removePlant((AbstractPlant) sprite);
    }

    @Override
    public void addSprite(ISprite sprite) throws IllegalArgumentException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) placeAnimal(animal);
        // If a sprite is a plant object
        else if (sprite instanceof AbstractPlant) placePlant((AbstractPlant) sprite);
    }

    @Override
    public MapArea getAreaType(Vector2D position) {
        if (position.precedes(jungleUpperRight) && position.follows(jungleLowerleft)) return MapArea.JUNGLE;
        else if (position.precedes(mapUpperRight) && position.follows(mapLowerLeft))  return MapArea.STEPPE;
        return null;
    }

    @Override
    public void spawnPlants() {
        spawnSinglePlant(MapArea.JUNGLE);
        spawnSinglePlant(MapArea.STEPPE);
    }

    @Override
    public void update() {
        feedAnimals();
        spawnPlants();
    }

    private void placeAnimal(Animal animal) {
        Vector2D position = animal.getCurrPosition();
        // Create the new animals list if there is no animals list on the specified position
        if (mapAnimals.get(position) == null) mapAnimals.put(position, new TreeSet<>(new EnergyComparator()));
        // Add an animal to the list
        mapAnimals.get(position).add(animal);
        // Add eaten plants to the list awaiting update
        AbstractPlant plant = mapPlants.get(position);
        if (plant != null) eatenPlants.add(plant);
    }

    private void removeAnimal(Animal animal, Vector2D position) throws NoSuchElementException {
        Set<Animal> animals = mapAnimals.get(position);
        if (animals == null || !animals.remove(animal)) {
            throw new NoSuchElementException("Sprite " + animal + " is not on the map");
        }
    }

    private void placePlant(AbstractPlant plant) throws IllegalArgumentException {
        Vector2D position = plant.getPosition();
        // Place plant on a field only if there is no other element occupying this field
        if (mapPlants.get(position) != null || mapAnimals.get(position) != null) {
            String message = "Cannot place plant on field: " + position + ". Field is not empty.";
            throw new IllegalArgumentException(message);
        }
        mapPlants.put(position, plant);
    }

    private void removePlant(AbstractPlant plant) throws NoSuchElementException {
        Vector2D position = plant.getPosition();
        if (mapPlants.remove(position) == null) {
            throw new NoSuchElementException("Plant " + plant + " is not on the map");
        }
    }

    private void feedAnimals() {
        for (AbstractPlant plant: eatenPlants) {
            // From the animals on the current field, choose
            // only ones that have the most energy
            List<Animal> animals = getAnimalsToFeed(plant.getPosition());
            // Divide the plant's energy into equal parts for
            // each selected animal
            int energyPart = plant.getEnergy() / animals.size();
            // Loop over all selected animals and feed them
            for (Animal animal: animals) animal.feed(energyPart);
            plant.remove();
        }
        eatenPlants.clear();
    }

    private List<Animal> getAnimalsToFeed(Vector2D position) {
        List<Animal> animals = new ArrayList<>();
        int maxEnergy = 0;
        // Animals are sorted in a non-increasing order, so a loop
        // below will always take the first animal (the one with the
        // greatest energy) and all others having the same energy value
        for (Animal animal: mapAnimals.get(position)) {
            if (animal.getEnergy() < maxEnergy) break;
            maxEnergy = animal.getEnergy();
            animals.add(animal);
        }
        return animals;
    }

    public boolean isEmptyField(Vector2D position) {
        return mapAnimals.get(position) == null
                || mapAnimals.get(position).size() == 0
                || mapPlants.get(position) == null;
    }

    private void spawnSinglePlant(MapArea area) {
        Vector2D position = switch (area) {
            case JUNGLE -> getJungleEmptyFieldVector();
            case STEPPE -> getSteppeEmptyFieldVector();
        };
        // Do not spawn a grass object if there is no more space available
        if (position == null) return;
        // Create a plant object and add it to the map
        ISprite plant = switch (area) {
            case JUNGLE -> new Bush(this, position, bushEnergy);
            case STEPPE -> new Grass(this, position, grassEnergy);
        };
        plant.add();
    }

    private Vector2D getJungleEmptyFieldVector() {
        return getSegmentEmptyFieldVector(null, jungleLowerleft, jungleUpperRight);
    }

    private Vector2D getSteppeEmptyFieldVector() {
        /*
        *  +---+---+---+
        *  | 0 | 1 | 2 |
        *  +---+---+---+
        *  | 3 |   | 4 |
        *  +---+---+---+
        *  | 5 | 6 | 7 |
        *  +---+---+---+
        */
        int segmentsCount = 8;
        int segmentIdx = Random.randInt(segmentsCount - 1);

        Vector2D position = null;
        int count = 0;
        while (count++ < segmentsCount && position == null) {
            Vector2D segmentLowerLeft  = getSegmentLowerLeft(segmentIdx);
            Vector2D segmentUpperRight = getSegmentUpperRight(segmentIdx);
            Vector2D initialPosition   = count > 0 ? segmentLowerLeft : null;
            position = getSegmentEmptyFieldVector(initialPosition, segmentLowerLeft, segmentUpperRight);
            segmentIdx = (segmentIdx + 1) % segmentsCount;
        }
        return position;
    }

    private Vector2D getSegmentLowerLeft(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> new Vector2D(mapLowerLeft.getX(), jungleUpperRight.getY());
            case 1 -> jungleLowerleft.upperLeft(jungleUpperRight);
            case 2 -> jungleUpperRight;
            case 3 -> new Vector2D(mapLowerLeft.getX(), jungleLowerleft.getY());
            case 4 -> jungleLowerleft.lowerRight(jungleUpperRight);
            case 5 -> mapLowerLeft;
            case 6 -> new Vector2D(jungleLowerleft.getX(), mapLowerLeft.getY());
            case 7 -> mapLowerLeft.lowerRight(mapUpperRight);
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentUpperRight(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> new Vector2D(jungleLowerleft.getX(), mapUpperRight.getY());
            case 1 -> new Vector2D(jungleUpperRight.getX(), mapUpperRight.getY());
            case 2 -> mapUpperRight;
            case 3 -> jungleLowerleft.upperLeft(jungleUpperRight);
            case 4 -> new Vector2D(mapUpperRight.getX(), jungleUpperRight.getY());
            case 5 -> jungleLowerleft;
            case 6 -> new Vector2D(jungleLowerleft.getX(), mapLowerLeft.getY());
            case 7 -> new Vector2D(mapUpperRight.getX(), jungleLowerleft.getY());
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentEmptyFieldVector(Vector2D initialPosition, Vector2D lowerLeft, Vector2D upperRight) {
        int minX = lowerLeft.getX();
        int maxX = upperRight.getX();
        int minY = lowerLeft.getY();
        int maxY = upperRight.getY();
        // Choose the first vector randomly if is not specified
        Vector2D position = initialPosition == null ? Vector2D.randomVector(minX, maxX, minY, maxY) : initialPosition;
        // Calculate segment dimensions
        int segmentWidth = maxX - minX + 1;
        int segmentHeight = maxY - minY + 1;
        int i = initialPosition.getX() * segmentHeight + initialPosition.getY();
        int segmentFieldsCount = segmentWidth * segmentHeight;
        // Loop over subsequent fields till a field is not empty
        while (!isEmptyField(position)) {
            i = (i + 1) % segmentFieldsCount;
            position = lowerLeft.add(new Vector2D(i / segmentHeight, i % segmentWidth));
        }
        return position;
    }
}
