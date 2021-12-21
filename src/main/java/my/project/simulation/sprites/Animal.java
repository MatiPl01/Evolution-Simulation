package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.GuiAnimalSprite;
import my.project.simulation.utils.AnimalTracker;
import my.project.simulation.utils.IObserver;
import my.project.simulation.maps.IMap;
import my.project.simulation.enums.MapDirection;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animal extends AbstractSprite {
    private static final int MIN_GENE_NUM = 0;
    private static final int MAX_GENE_NUM = 7;
    private static final int GENES_COUNT = 32;
    private static final double BREED_ENERGY_LOSS_RATIO = .25;
    private final String IMG_PATH = "src/main/resources/images/animals/leopard.png";

    private final int[] genome;
    private final int[] genesCounts;

    private Vector2D prevPosition;
    private MapDirection direction;
    private int energy;
    private int daysAlive = 0;
    private int childrenCount = 0;
    private AnimalTracker tracker;
    // ID is a unique number referring to the animal
    // (this number also indicates in which order animals appeared on a map)
    private final long ID;

    public Animal(IMap map, Vector2D initialPosition) {
        super(map, initialPosition);
        this.ID = map.getNewAnimalID();
        this.prevPosition = initialPosition;
        this.genome = generateRandomGenome();
        this.energy = map.getStartEnergy();
        this.direction = generateRandomDirection();
        this.genesCounts = calculateGenesCounts(genome);
        addObserver(new GuiAnimalSprite(this));
    }

    public Animal(IMap map, Vector2D initialPosition, int energy, int[] genome) {
        super(map, initialPosition);
        this.ID = map.getNewAnimalID();
        this.genome = genome;
        this.energy = energy;
        this.prevPosition = initialPosition;
        this.direction = generateRandomDirection();
        this.genesCounts = calculateGenesCounts(genome);
        addObserver(new GuiAnimalSprite(this));
    }

    @Override
    public String toString() {
        return switch (direction) {
            case NORTH -> "N";
            case NORTHEAST -> "NE";
            case EAST -> "E";
            case SOUTHEAST -> "SE";
            case SOUTH -> "S";
            case SOUTHWEST -> "SW";
            case WEST -> "W";
            case NORTHWEST -> "NW";
        };
    }

    @Override
    public String getImagePath() {
        return IMG_PATH;
    }

    public Vector2D getPrevPosition() {
        return prevPosition;
    }

    public int getEnergy() {
        return energy;
    }

    public long getID() {
        return ID;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public List<Integer> getGenome() {
        return Arrays.stream(genome).boxed().toList();
    }

    public int getDaysAlive() {
        return daysAlive;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public static List<Integer> getPossibleGenes() {
        List<Integer> genes = new ArrayList<>();
        for (int i = MIN_GENE_NUM; i <= MAX_GENE_NUM; i++) genes.add(i);
        return genes;
    }

    public void setTracker(AnimalTracker tracker) {
        this.tracker = tracker;
    }

    public void removeTracker() {
        tracker = null;
    }

    public void update() {
        // Update animal's state only if an animal is alive
        // (only if it has energy greater than 0)
        if (energy <= 0) {
            if (tracker != null && tracker.getTrackedAnimal() == this) tracker.recordAnimalDeath();
            remove();
        } else {
            int angleNum = chooseRotationAngleNum();
            decreaseEnergy(map.getMoveEnergy());
            rotate(angleNum);
            if (canMove(angleNum)) move();
            daysAlive++;
        }
    }

    public void feed(int deltaEnergy) {
        energy += deltaEnergy;
    }

    public void rotate(int angleNum) {
        direction = direction.rotate(angleNum);
        prevPosition = position;
        notifyPositionChanged();
    }

    private boolean canMove(int angleNum) {
        return angleNum == 0 || angleNum == MapDirection.values().length / 2;
    }

    public void move() {
        Vector2D moveVector = direction.toUnitVector();
        Vector2D newPosition = map.getNextPosition(position, moveVector);
        // Move an animal only if a new position will be different to the current one
        prevPosition = position;
        position = newPosition;
        notifyPositionChanged();
    }

    private void notifyPositionChanged() {
        for (IObserver observer : observers) observer.changeSpritePosition(this);
    }

    private int[] calculateGenesCounts(int[] currGenome) {
        // counts[i] - total number of occurrences of digits that are
        //             not greater than i
        int[] counts = new int[MAX_GENE_NUM - MIN_GENE_NUM + 1];
        int j = 0;
        for (int i = 0; i <= MAX_GENE_NUM - MIN_GENE_NUM; i++) {
            while (j < genome.length && currGenome[j] == i) {
                j++;
            }
            counts[i - MIN_GENE_NUM] = j;
        }
        return counts;
    }

    private int chooseRotationAngleNum() {
        // Using Binary Search, look for the next random rotation angle
        // considering animal's preferences
        return binarySearchGE(genesCounts, 0, MAX_GENE_NUM - MIN_GENE_NUM, Random.randInt(1, GENES_COUNT));
    }

    // Look for an index of the lowest number that is not lower than the target
    private int binarySearchGE(int[] arr, int l, int r, int target) {
        int m = (l + r) / 2;
        if (l == r) return r;
        if (target > arr[m]) return binarySearchGE(arr, m + 1, r, target);
        return binarySearchGE(arr, l, m, target);
    }

    private MapDirection generateRandomDirection() {
        MapDirection[] values = MapDirection.values();
        return values[Random.randInt(values.length - 1)];
    }

    private int[] generateRandomGenome() {
        int[] newGenome = new int[GENES_COUNT];
        for (int i = 0; i < GENES_COUNT; i++) {
            newGenome[i] = (Random.randInt(MIN_GENE_NUM, MAX_GENE_NUM));
        }
        Arrays.sort(newGenome);
        return newGenome;
    }

    private static int[] mergeGenomes(int[] genome1, int energy1,
                                      int[] genome2, int energy2) {
        // Randomly select the part of the genes belonging to the stronger parent
        if (Random.random() < .5) {
            // Swap genomes
            int[] temp1 = genome1;
            genome1 = genome2;
            genome2 = temp1;
            // Swap energies
            int temp2 = energy1;
            energy1 = energy2;
            energy2 = temp2;
        }
        // Split parents genomes and merge them into a new genome
        int splitIdx = (energy1 * GENES_COUNT) / (energy1 + energy2);
        int[] newGenome = new int[GENES_COUNT];
        for (int i = 0; i < splitIdx; i++) newGenome[i] = genome1[i];
        for (int i = splitIdx; i < GENES_COUNT; i++) newGenome[i] = genome2[i];
        return newGenome;
    }

    private void decreaseEnergy(int deltaEnergy) {
        energy -= deltaEnergy;
    }

    public boolean canBreed() {
        return energy >= map.getMinBreedEnergy();
    }

    public void breed(Animal other) {
        // Do not bread if at least one of animals has not enough energy
        if (!canBreed() || !other.canBreed()) return;
        // Calculate the energy lost by parents during reproduction
        int deltaEnergy1 = (int) (energy * BREED_ENERGY_LOSS_RATIO);
        int deltaEnergy2 = (int) (other.energy * BREED_ENERGY_LOSS_RATIO);
        // Decrease parents energy
        decreaseEnergy(deltaEnergy1);
        other.decreaseEnergy(deltaEnergy2);
        // Create the genome of the child
        int[] childGenome = mergeGenomes(genome, energy, other.genome, other.energy);
        // Create a new animal with the energy inherited from its parents
        Animal child = new Animal(map, position, deltaEnergy1 + deltaEnergy2, childGenome);
        // Increment a number of parent's children
        childrenCount++;
        other.childrenCount++;
        child.add();
        // Handle animal tracking (if at least one of child's parents is tracked)
        handleTracking(other, child);
    }

    private void handleTracking(Animal other, Animal child) {
        // Enable child tracking if one of parents is tracked
        if (tracker != null || other.tracker != null) {
            child.setTracker(tracker);
            tracker.addToTrackedList(child);
            // Increment a number of children of the tracked animal if one
            // of created animal's parents is tracked
            Animal trackedAnimal = tracker.getTrackedAnimal();
            if (trackedAnimal == this || trackedAnimal == other) tracker.incrementChildrenCount();
        }
    }
}
