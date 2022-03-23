package my.project.simulation.maps;

import javafx.scene.control.Label;
import my.project.gui.config.MapSettings;
import my.project.gui.simulation.grid.IBuilder;
import my.project.gui.utils.InfoLogger;
import my.project.simulation.enums.MapStrategy;
import my.project.simulation.stats.StatsMeter;
import my.project.simulation.utils.*;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.*;
import my.project.simulation.utils.Random;
import my.project.simulation.datastructures.PrefixTree;

import java.util.*;

public abstract class AbstractMap implements IMap, IObserver {
    private static final double MIN_BREED_ENERGY_RATIO = .5;
    private static final int INFO_DISPLAY_TIME = 5000; // ms
    private static final String DEFAULT_INFO_MESSAGE = "";

    private final MapSettings settings;
    private final int magicStrategyRespawnThreshold;
    private final int maxMagicRespawnsCount;
    private final String mapName;

    private final int width;
    private final int height;
    private final int moveEnergy;
    private final int startEnergy;
    private final int bushEnergy;
    private final int grassEnergy;

    protected final Vector2D mapLowerLeft = new Vector2D(0, 0);
    protected final Vector2D mapUpperRight;
    protected final Vector2D jungleLowerleft;
    protected final Vector2D jungleUpperRight;

    public final Map<Vector2D, SortedSet<Animal>> mapAnimals = new HashMap<>();
    protected final Map<Vector2D, AbstractPlant> mapPlants = new HashMap<>();
    protected final Set<AbstractPlant> eatenPlants = new HashSet<>();
    protected final PrefixTree<Integer, Animal> genomesTree = new PrefixTree<>(Animal.getPossibleGenes());

    protected final int fieldsCount;
    private final int initialAnimalsCount;
    private final MapStrategy strategy;
    private int magicRespawnsCount = 0;

    private long dayNum = 0;
    private long animalsCount;
    private long diedAnimalsCount = 0;
    private long plantsCount = 0;
    private long diedAnimalsSumDaysAlive = 0;

    protected StatsMeter statsMeter;
    private IBuilder gridBuilder;
    private AnimalTracker animalTracker;
    private InfoLogger infoLogger;

    AbstractMap(String mapName, MapSettings mapSettings) {
        // Store initial values
        this.settings = mapSettings;
        this.mapName = mapName;
        this.width = mapSettings.width();
        this.height = mapSettings.height();
        this.initialAnimalsCount = mapSettings.animalsCount();
        this.startEnergy = mapSettings.startEnergy();
        this.moveEnergy = mapSettings.moveEnergy();
        this.bushEnergy = mapSettings.bushEnergy();
        this.grassEnergy = mapSettings.grassEnergy();
        this.magicStrategyRespawnThreshold = mapSettings.magicRespawnAnimals();
        this.maxMagicRespawnsCount = mapSettings.magicRespawnsCount();
        this.strategy = mapSettings.mapStrategy();

        // Calculate map upper-right bound vector
        this.mapUpperRight = new Vector2D(width - 1, height - 1);

        // Calculate jungle bounding vectors and a number of fields
        double jungleRatio = mapSettings.jungleRatio();
        int jungleWidth = (int)Math.round(width * jungleRatio);
        if (jungleWidth % 2 != width % 2) jungleWidth += 1;
        int jungleHeight = (int)Math.round(height * jungleRatio);
        if (jungleHeight % 2 != height % 2) jungleHeight += 1;
        this.jungleLowerleft = new Vector2D((width - jungleWidth) / 2, (height - jungleHeight) / 2);
        this.jungleUpperRight = this.jungleLowerleft.add(new Vector2D(jungleWidth - 1, jungleHeight - 1));

        // Calculate a total number of map fields
        this.fieldsCount = (mapUpperRight.getX() - mapLowerLeft.getX() + 1)
                         * (mapUpperRight.getY() - mapLowerLeft.getY()) + 1;

    }

    @Override
    public void changeSpritePosition(ISprite sprite) throws IllegalArgumentException, NoSuchElementException {
        if (!(sprite instanceof Animal animal)) {
            throw new IllegalArgumentException("Cannot change position of a sprite which is not an animal");
        } else {
            // Remove an animal from the previous position on a map
            removeAnimal(animal, animal.getPrevPosition(), true);
            // Add an animal at a new position on the map
            placeAnimal(animal);
        }
    }

    @Override
    public void removeSprite(ISprite sprite) throws NoSuchElementException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) {
            removeAnimal(animal, animal.getPosition(), true);
            diedAnimalsCount++;
            diedAnimalsSumDaysAlive += animal.getDaysAlive();
            genomesTree.remove(animal.getGenome(), animal);
            // If a strategy is set to magic and conditions are fulfilled
            if (meetsMagicRespawnConditions()) handleMagicRespawn();
        }
        // If a sprite is a plant object
        else if (sprite instanceof AbstractPlant) {
            removePlant((AbstractPlant) sprite);
            plantsCount--;
        };
    }

    @Override
    public void addSprite(ISprite sprite) throws IllegalArgumentException {
        // If a sprite is an animal object
        if (sprite instanceof Animal animal) {
            placeAnimal(animal);
            animalsCount++;
            genomesTree.add(animal.getGenome(), animal);
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setGridBuilder(IBuilder gridBuilder) {
        this.gridBuilder = gridBuilder;
    }

    @Override
    public IBuilder getGridBuilder() {
        return gridBuilder;
    }

    @Override
    public StatsMeter getStatsMeter() {
        return statsMeter;
    }

    @Override
    public void spawnPlants() {
        spawnSinglePlant(MapArea.JUNGLE);
        spawnSinglePlant(MapArea.STEPPE);
    }

    @Override
    public long getNewAnimalID() {
        return animalsCount + 1;
    }

    @Override
    public void update() {
        if (areAnimalsAlive()) {
            dayNum++;
            updateAnimals();
            feedAnimals();
            breedAnimals();
            spawnPlants();
            updateStatistics();
        }
    }

    @Override
    public boolean areAnimalsAlive() {
        return getAnimalsAliveCount() > 0;
    }

    @Override
    public void setAnimalTracker(AnimalTracker tracker) {
        if (animalTracker != null) animalTracker.remove();
        animalTracker = tracker;
    }

    @Override
    public void removeAnimalTracker() {
        if (animalTracker != null) {
            animalTracker.remove();
            animalTracker = null;
        }
        else throw new NoSuchElementException("There is no AnimalTracker set up in a map");
    }

    @Override
    public AnimalTracker getAnimalTracker() {
        return animalTracker;
    }

    @Override
    public long getCurrentDayNum() {
        return dayNum;
    }

    @Override
    public Set<List<Integer>> getDominantGenomes() {
        return genomesTree.getMaxCountKeys();
    }

    @Override
    public Set<Animal> getAnimalsWithGenome(List<Integer> genome) {
        return genomesTree.getValues(genome);
    }

    @Override
    public Set<Animal> getDominantGenomesAnimals() {
        return genomesTree.getMaxCountValues();
    }

    @Override
    public Set<Animal> getAllAnimals() {
        // Get animals sorted in a non-decreasing order
        Set<Animal> result = new TreeSet<>(new MinEnergyComparator());
        for (Set<Animal> animals: mapAnimals.values()) result.addAll(animals);
        return result;
    }

    @Override
    public Set<Animal> getMaxEnergyFieldAnimals() {
        Set<Animal> animals = new HashSet<>();
        for (Vector2D position: mapAnimals.keySet()) {
            Animal animal = getMaxEnergyFieldAnimal(position);
            if (animal != null) animals.add(animal);
        }
        return animals;
    }

    @Override
    public Animal getMaxEnergyFieldAnimal(Vector2D position) {
        return mapAnimals.get(position) != null ? mapAnimals.get(position).first() : null;
    }

    @Override
    public Set<Animal> getAllFieldAnimals(Vector2D position) {
        return mapAnimals.get(position) != null ? mapAnimals.get(position) : new TreeSet<>(new MaxEnergyComparator());
    }

    @Override
    public void setInfoBox(Label infoBox) {
        this.infoLogger = new InfoLogger(infoBox);
        infoLogger.setDefaultText(DEFAULT_INFO_MESSAGE);
        statsMeter.setInfoLogger(infoLogger);
    }

    @Override
    public List<Vector2D> getMapBoundingRect() {
        return new ArrayList<>() {{
            add(mapLowerLeft);
            add(mapUpperRight);
            add(jungleLowerleft);
            add(jungleUpperRight);
        }};
    }

    @Override
    public int getMoveEnergy() {
        return moveEnergy;
    }

    @Override
    public int getStartEnergy() {
        return startEnergy;
    }

    @Override
    public int getMinBreedEnergy() {
        return (int)(startEnergy * MIN_BREED_ENERGY_RATIO + .5);
    }

    @Override
    public void initialize() {
        randomlyPlaceAnimals(initialAnimalsCount);
        spawnPlants();
        updateStatistics();
    }

    @Override
    public MapSettings getSettings() {
        return settings;
    }

    private void randomlyPlaceAnimals(int animalsCount) {
        int minX = mapLowerLeft.getX();
        int maxX = mapUpperRight.getX();
        int minY = mapLowerLeft.getY();
        int maxY = mapUpperRight.getY();

        for (int i = 0; i < animalsCount; i++) {
            Vector2D randomPosition = Vector2D.randomVector(minX, maxX, minY, maxY);
            Vector2D position = getSegmentEmptyFieldVector(randomPosition, mapLowerLeft, mapUpperRight, true);
            Animal animal = new Animal(this, position);
            animal.add();
        }
    }

    private void placeAnimal(Animal animal) {
        Vector2D position = animal.getPosition();
        // Create the new animals list if there is no animals list on the specified position
        if (mapAnimals.get(position) == null) mapAnimals.put(position, new TreeSet<>(new MaxEnergyComparator()));
        // Add an animal to the list
        mapAnimals.get(position).add(animal);
        // Add eaten plants to the list awaiting update
        AbstractPlant plant = mapPlants.get(position);
        if (plant != null) eatenPlants.add(plant);
    }

    private void removeAnimal(Animal animal, Vector2D position, boolean removeSetIfEmpty) throws NoSuchElementException {
        Set<Animal> animals = mapAnimals.get(position);
        if (animals != null && animals.contains(animal)) animals.remove(animal);
        // Sometimes Set goes crazy when dealing with mutable objects
        // and can't see that they store a particular object. In such case,
        // we will compare all animals from a set one by one with the animal
        // which will be removed.
        else {
            if (animals != null) {
                Iterator<Animal> it = animals.iterator();
                while (it.hasNext()) {
                    Animal currentAnimal = it.next();
                    // Remove the current animal if found an animal with the same
                    // id as an animal which we want to remove
                    if (currentAnimal.getID() == animal.getID()) {
                        it.remove();
                        return;
                    }
                }
            }
            // Throw an exception if the desired animal wasn't found
            throw new NoSuchElementException("Sprite " + animal + " is not on the map");
        }
        // Remove the whole set if there are no more animals
        if (removeSetIfEmpty && animals.size() == 0) mapAnimals.remove(position);
    }

    private void placePlant(AbstractPlant plant) throws IllegalArgumentException {
        Vector2D position = plant.getPosition();
        // Place plant on a field only if there is no other element occupying this field
        if (!isEmptyField(position, false)) {
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
        // Get a set of animals sorted in a non-decreasing order to ensure
        // that animals with the greatest energy value will be updated at the
        // end. This guarantees that when there is more than one animal at
        // a particular field, the on with the greatest energy will be displayed
        // at the top.
        for (Animal animal: getAllAnimals()) animal.update();
    }

    private void feedAnimals() {
        for (AbstractPlant plant: eatenPlants) {
            // From the animals on the current field, choose
            // only ones that have the most energy
            List<Animal> animals = getAnimalsWithMaxEnergy(plant.getPosition(), 1);
            // Divide the plant's energy into equal parts for
            // each selected animal
            try {
                int energyPart = plant.getEnergy() / animals.size();
                // Loop over all selected animals and feed them
                for (Animal animal: animals) animal.feed(energyPart);
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }
            plant.remove();
        }
        eatenPlants.clear();
    }

    private void breedAnimals() {
        for (Vector2D position: new ArrayList<>(mapAnimals.keySet())) {
            Set<Animal> animals = mapAnimals.get(position);
            // Continue if there are not enough animals to breed on one field
            if (animals.size() < 2) continue;
            // Otherwise, find 2 animals with the greatest energy value
            List<Animal> animalsToBreed = getAnimalsToBreed(position);
            // Continue if there are no enough animals on the current field
            if (animalsToBreed == null) continue;
            Animal parent1 = animalsToBreed.get(0);
            Animal parent2 = animalsToBreed.get(1);
            parent1.breed(parent2);
            // To ensure that the order of animals in a mapAnimals TreeSet
            // is non-decreasing, we have to remove animals which are parents
            // and add them again with modified energy value
            removeAnimal(parent1, position, false);
            removeAnimal(parent2, position, false);
            placeAnimal(parent1);
            placeAnimal(parent2);
        }
    }

    private List<Animal> getAnimalsWithMaxEnergy(Vector2D position, int firstMaxValuesCount) {
        List<Animal> result = new ArrayList<>();
        Set<Animal> animals = mapAnimals.get(position);
        int maxEnergy = -moveEnergy;
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

    public boolean isEmptyField(Vector2D position, boolean allowPlants) {
        return (mapAnimals.get(position) == null || mapAnimals.get(position).size() == 0)
                && (allowPlants || mapPlants.get(position) == null);
    }

    private void spawnSinglePlant(MapArea area) {
        Vector2D position = switch (area) {
            case JUNGLE -> getJungleEmptyFieldVector(false);
            case STEPPE -> getSteppeEmptyFieldVector(false);
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

    private Vector2D getJungleEmptyFieldVector(boolean allowPlants) {
        Vector2D initialPosition = Vector2D.randomVector(jungleLowerleft.getX(), jungleUpperRight.getX(),
                                                         jungleLowerleft.getY(), jungleUpperRight.getY());
        return getSegmentEmptyFieldVector(initialPosition, jungleLowerleft, jungleUpperRight, allowPlants);
    }

    private Vector2D getSteppeEmptyFieldVector(boolean allowPlants) {
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
        int segmentIdx = getRandomSegmentIndex();

        Vector2D position = null;
        int count = 0;
        while (count++ < segmentsCount && position == null) {
            Vector2D segmentLowerLeft  = getSegmentLowerLeft(segmentIdx);
            Vector2D segmentUpperRight = getSegmentUpperRight(segmentIdx);
            Vector2D initialPosition = Vector2D.randomVector(segmentLowerLeft.getX(), segmentUpperRight.getX(),
                                                             segmentLowerLeft.getY(), segmentUpperRight.getY());
            // Continue if the area is a jungle (for example, when there is no segment with
            // specified segmentIdx, because a jungle occupies a whole (or almost whole) map)
            position = getSegmentEmptyFieldVector(initialPosition, segmentLowerLeft, segmentUpperRight, allowPlants);
            segmentIdx = (segmentIdx + 1) % segmentsCount;
        }
        return position;
    }

    private int getRandomSegmentIndex() {
        int randX = Random.randInt(mapLowerLeft.getX(), mapUpperRight.getX());
        int randY = Random.randInt(mapLowerLeft.getY(), mapUpperRight.getY());
        if (randX < jungleLowerleft.getX() || randX > jungleUpperRight.getX()) {
            if (randY < jungleLowerleft.getY()) return Random.randInt(1) == 0 ? 5 : 7;
            if (randY > jungleUpperRight.getY()) return Random.randInt(1) == 0 ? 0 : 2;
            return Random.randInt(1) == 0 ? 3 : 4;
        } else if (randY < jungleLowerleft.getY() || randY > jungleUpperRight.getY()) {
            return Random.randInt(1) == 0 ? 1 : 6;
        } else return Random.randInt(7);
    }

    private Vector2D getSegmentLowerLeft(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> mapLowerLeft.upperLeft(jungleUpperRight).add(new Vector2D(0, 1));
            case 1 -> jungleLowerleft.upperLeft(jungleUpperRight).add(new Vector2D(0, 1));
            case 2 -> jungleUpperRight.add(new Vector2D(1, 1));
            case 3 -> mapLowerLeft.upperLeft(jungleLowerleft);
            case 4 -> jungleLowerleft.lowerRight(jungleUpperRight).add(new Vector2D(1, 0));
            case 5 -> mapLowerLeft;
            case 6 -> mapLowerLeft.lowerRight(jungleLowerleft);
            case 7 -> mapLowerLeft.lowerRight(jungleUpperRight).add(new Vector2D(1, 0));
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentUpperRight(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> jungleLowerleft.upperLeft(mapUpperRight).subtract(new Vector2D(1, 0));
            case 1 -> jungleUpperRight.upperLeft(mapUpperRight);
            case 2 -> mapUpperRight;
            case 3 -> jungleLowerleft.upperLeft(jungleUpperRight).subtract(new Vector2D(1, 0));
            case 4 -> jungleUpperRight.lowerRight(mapUpperRight);
            case 5 -> jungleLowerleft.subtract(new Vector2D(1, 1));
            case 6 -> jungleUpperRight.lowerRight(jungleLowerleft).subtract(new Vector2D(0, 1));
            case 7 -> jungleLowerleft.lowerRight(mapUpperRight).subtract(new Vector2D(0, 1));
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentEmptyFieldVector(Vector2D position, Vector2D lowerLeft, Vector2D upperRight, boolean allowPlants) {
        int minX = lowerLeft.getX();
        int maxX = upperRight.getX();
        int minY = lowerLeft.getY();
        int maxY = upperRight.getY();
        // Calculate segment dimensions
        int segmentWidth = maxX - minX + 1;
        int segmentHeight = maxY - minY + 1;

        // Return null is a segment doesn't exist
        if (segmentHeight == 0 || segmentWidth == 0) return null;
        int i = position.getX() * segmentHeight + position.getY();
        int count = 0;
        int segmentFieldsCount = segmentWidth * segmentHeight;
        // Loop over subsequent fields till a field is not empty
        while (!isEmptyField(position, allowPlants)) {
            i = (i + 1) % segmentFieldsCount;
            position = lowerLeft.add(new Vector2D(i / segmentHeight, i % segmentHeight));
            // Return null if cannot find an empty field in a segment
            if (count++ == segmentFieldsCount) return null;
        }
        return position;
    }

    private long getAnimalsAliveCount() {
        return animalsCount - diedAnimalsCount;
    }

    private double calcAverageAliveEnergy() {
        long energySum = 0;
        for (Set<Animal> animals: mapAnimals.values()) {
            for (Animal animal: animals) energySum += animal.getEnergy();
        }
        return energySum > 0 ? 1. * energySum / getAnimalsAliveCount() : 0;
    }

    private double calcAverageAliveChildrenCount() {
        long childrenSum = 0;
        for (Set<Animal> animals: mapAnimals.values()) {
            for (Animal animal: animals) childrenSum += animal.getChildrenCount();
        }
        return childrenSum > 0 ? 1. * childrenSum / getAnimalsAliveCount() : 0;
    }

    private double calcAverageDiedAnimalsLifespan() {
        if (diedAnimalsCount == 0) return 0; // 0 means there are no animals died
        return 1. * diedAnimalsSumDaysAlive / diedAnimalsCount;
    }

    private void updateStatistics() {
        statsMeter.updateStatistics(getAnimalsAliveCount(),
                plantsCount,
                getDominantGenomes(),
                calcAverageAliveEnergy(),
                calcAverageDiedAnimalsLifespan(),
                calcAverageAliveChildrenCount());
    }

    private boolean meetsMagicRespawnConditions() {
        return (strategy == MapStrategy.MAGIC &&
                animalsCount - diedAnimalsCount == magicStrategyRespawnThreshold &&
                magicRespawnsCount++ < maxMagicRespawnsCount);
    }

    private void handleMagicRespawn() {
        infoLogger.displayInfo( "Performed magic respawn on " + mapName, INFO_DISPLAY_TIME);
        // Spawn remaining animals copies if magic strategy was chosen
        int animalsAliveCount = (int)(animalsCount - diedAnimalsCount);
        int remainingEmptyFields = fieldsCount - animalsAliveCount;
        Set<Animal> animalsAlive = getAllAnimals();
        Iterator<Animal> animalsIt = animalsAlive.iterator();
        int i = 0;
        while (i < remainingEmptyFields && animalsIt.hasNext()) {
            Animal currAnimal = animalsIt.next();
            Vector2D position = Vector2D.randomVector(mapLowerLeft.getX(), mapUpperRight.getX(),
                                                      mapLowerLeft.getY(), mapUpperRight.getY());
            position = getSegmentEmptyFieldVector(position, mapLowerLeft, mapUpperRight, true);
            if (position == null) return;
            (new Animal(this, position, startEnergy, currAnimal.getGenome())).add();
        }
    }
}
