package my.project.simulation.maps;

import my.project.simulation.data.structures.PrefixTree;
import my.project.simulation.utils.IObserver;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.*;
import my.project.simulation.utils.EnergyComparator;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.*;

public abstract class AbstractMap implements IMap, IObserver {
    private static final double MIN_BREED_ENERGY_RATIO = .5;
    private final int moveEnergy;
    private final int startEnergy;
    private final int bushEnergy;
    private final int grassEnergy;

    protected final Vector2D mapLowerLeft = new Vector2D(0, 0);
    protected final Vector2D mapUpperRight;
    protected final Vector2D jungleLowerleft;
    protected final Vector2D jungleUpperRight;

    protected final Map<Vector2D, SortedSet<Animal>> mapAnimals = new HashMap<>();
    protected final Map<Vector2D, AbstractPlant> mapPlants = new HashMap<>();
    protected final List<AbstractPlant> eatenPlants = new ArrayList<>();
    protected final PrefixTree<Integer, Animal> genotypesTree = new PrefixTree<>(Animal.getPossibleGenes());

    private int animalsAliveCount;
    private int animalsDiedCount = 0;
    private int plantsCount = 0;

    AbstractMap(int width, int height, double jungleRatio,
                int startEnergy, int moveEnergy, int bushEnergy, int grassEnergy,
                int animalsCount) {
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.bushEnergy = bushEnergy;
        this.grassEnergy = grassEnergy;
        this.animalsAliveCount = animalsCount;
        this.mapUpperRight = new Vector2D(width, height);
        int jungleWidth  = (int) (2 * Math.round((width / 2) * jungleRatio) + (width % 2 == 1 ? 1 : 0));
        int jungleHeight = (int) (2 * Math.round((height / 2) * jungleRatio) + (height % 2 == 1 ? 1 : 0));
        this.jungleLowerleft = new Vector2D((width - jungleWidth) / 2, (height - jungleHeight) / 2);
        this.jungleUpperRight = this.jungleLowerleft.add(new Vector2D(jungleWidth, jungleHeight));
        randomlyPalceAnimals(animalsCount);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) throws IllegalArgumentException, NoSuchElementException {
        if (!(sprite instanceof Animal animal)) {
            throw new IllegalArgumentException("Cannot change position of a sprite which is not an animal");
        } else {
            // Remove an animal from the previous position on a map
            removeAnimal(animal, animal.getPrevPosition());
            // Add an animal at a new position on the map
            placeAnimal(animal);
        }
    }

    @Override
    public void removeSprite(ISprite sprite) throws NoSuchElementException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) {
            removeAnimal(animal, animal.getCurrPosition());
            animalsDiedCount++;
            animalsAliveCount--;
            genotypesTree.remove(animal.getGenome());
        }
        // If a sprite is a plant object
        else if (sprite instanceof AbstractPlant) {
            removePlant((AbstractPlant) sprite);
            plantsCount--;
        }
    }

    @Override
    public void addSprite(ISprite sprite) throws IllegalArgumentException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) {
            placeAnimal(animal);
            animalsAliveCount++;
            genotypesTree.insert(animal.getGenome(), animal);
        }
        // If a sprite is a plant object
        else if (sprite instanceof AbstractPlant) {
            placePlant((AbstractPlant) sprite);
            plantsCount++;
        }
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
        spawnPlants();
        updateAnimals();
        feedAnimals();
        breedAnimals();
    }

    public List<Vector2D> getMapBoundingRect() {
        return new ArrayList<>() {{
            add(mapLowerLeft);
            add(mapUpperRight);
            add(jungleLowerleft);
            add(jungleUpperRight);
        }};
    }

    public List<List<Integer>> getDominantGenotypes() {
        return genotypesTree.getMaxCountKeys();
    }

    public Set<Animal> getDominantGenotypesAnimals() {
        return genotypesTree.getMaxCountValues();
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMinBreedEnergy() {
        return (int)(startEnergy * MIN_BREED_ENERGY_RATIO + .5);
    }

    private void randomlyPalceAnimals(int animalsCount) {
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapLowerLeft.getY();

        for (int i = 0; i < animalsCount; i++) {
            Vector2D randomPosition = Vector2D.randomVector(minX, maxX, minY, maxY);
            Vector2D position = getSegmentEmptyFieldVector(randomPosition, mapLowerLeft, mapUpperRight);
            Animal animal = new Animal(this, position);
            animal.add();
        }
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
        // Remove the whole entry in a mapAnimals if removed the last animal
        // from the current position
        if (animals.size() == 0) mapAnimals.remove(position);
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

    private void updateAnimals() {
        for (SortedSet<Animal> animals: mapAnimals.values()) {
            for (Animal animal: animals) animal.update();
        }
    }

    private void feedAnimals() {
        for (AbstractPlant plant: eatenPlants) {
            // From the animals on the current field, choose
            // only ones that have the most energy
            List<Animal> animals = getAnimalsWithMaxEnergy(plant.getPosition(), 1);
            // Divide the plant's energy into equal parts for
            // each selected animal
            int energyPart = plant.getEnergy() / animals.size();
            // Loop over all selected animals and feed them
            for (Animal animal: animals) animal.feed(energyPart);
            plant.remove();
        }
        eatenPlants.clear();
    }

    private void breedAnimals() {
        for (Vector2D position: mapAnimals.keySet()) {
            SortedSet<Animal> animals = mapAnimals.get(position);
            // Continue if there are not enough animals to breed on one field
            if (animals.size() < 2) continue;
            // Otherwise, find 2 animals with the greatest energy value
            List<Animal> animalsToBreed = getAnimalsToBreed(position);
            // Continue if there are no enough animals on the current field
            if (animalsToBreed == null) continue;
            animalsToBreed.get(0).breed(animalsToBreed.get(1));
        }
    }

    private List<Animal> getAnimalsWithMaxEnergy(Vector2D position, int firstMaxValuesCount) {
        List<Animal> result = new ArrayList<>();
        Set<Animal> animals = mapAnimals.get(position);
        int maxEnergy = 0;
        int count = 1;
        // Animals are sorted in a non-increasing order, so a loop
        // below will always take the first animal (the one with the
        // greatest energy) and all others having the same energy value
        if (animals != null) {
            for (Animal animal: animals) {
                if (animal.getEnergy() < maxEnergy && ++count > firstMaxValuesCount) break;
                maxEnergy = animal.getEnergy();
                result.add(animal);
            }
        }
        return result;
    }

    private List<Animal> getAnimalsToBreed(Vector2D position) {
        List<Animal> animals;
        // Try to get 2 animals with the same energy equal to the maximum energy
        animals = getAnimalsWithMaxEnergy(position, 1);
        // If there are not enough animals, take 2 that have different energy
        // value but greater than all the remaining animals
        if (animals.size() < 2)  animals = getAnimalsWithMaxEnergy(position, 2);
        if (animals.size() >= 2) return Random.sample(animals, 2);
        return null;
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
