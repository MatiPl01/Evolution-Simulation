package my.project.simulation.sprites;

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
    private final String IMG_PATH = "/images/animals/leopard.png";

    private Vector2D currPosition;
    private Vector2D prevPosition = null;
    private MapDirection direction;
    private final int[] genome;
    private final int[] genesCounts;
    private int energy;
    private int daysAlive = 0;
    private final List<Animal> children = new ArrayList<>();

    public Animal(IMap map, Vector2D initialPosition) {
        super(map);
        this.genome = generateRandomGenome();
        this.energy = map.getStartEnergy();
        this.currPosition = initialPosition;
        this.direction = generateRandomDirection();
        this.genesCounts = createRotationPreferences(genome);
    }

    public Animal(IMap map, Vector2D initialPosition, int energy, int[] genome) {
        super(map);
        this.genome = genome;
        this.energy = energy;
        this.currPosition = initialPosition;
        this.direction = generateRandomDirection();
        this.genesCounts = createRotationPreferences(genome);
    }

    @Override
    public String toString() {
        return switch(direction) {
            case NORTH     -> "N";
            case NORTHEAST -> "NE";
            case EAST      -> "E";
            case SOUTHEAST -> "SE";
            case SOUTH     -> "S";
            case SOUTHWEST -> "SW";
            case WEST      -> "W";
            case NORTHWEST -> "NW";
        };
    }

    @Override
    public String getImagePath() {
        return IMG_PATH;
    }

    public Vector2D getCurrPosition() {
        return currPosition;
    }

    public Vector2D getPrevPosition() {
        return prevPosition;
    }

    public int getEnergy() {
        return energy;
    }

    public int[] getGenome() {
        return genome;
    }

    public int getDaysAlive() {
        return daysAlive;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public long getDescendantsCount() {
        return countDescendants(this);
    }

    public void update() {
        // Update animal's state only if an animal is alive
        // (only if it has energy greater than 0)
        if (energy > 0) {
            int angleNum = chooseRotationAngleNum();
            rotate(angleNum);
            if (canMove(angleNum)) move();
            decreaseEnergy(map.getMoveEnergy());
            // Delete an animal if its energy dropped below 0
            if (energy <= 0) remove();
                // Otherwise, increment days alive counter
            else daysAlive++;
        }
    }

    public void feed(int deltaEnergy) {
        energy += deltaEnergy;
    }

    public void rotate(int angleNum) {
        direction = direction.rotate(angleNum);
        notifyPositionChanged();
    }

    private boolean canMove(int angleNum) {
        return angleNum == 0 || angleNum == MapDirection.values().length / 2;
    }

    public void move() {
        Vector2D moveVector = direction.toUnitVector();
        Vector2D nextPosition = map.getNextPosition(currPosition, moveVector);
        // Move an animal only if a new position will be different to the current one
        prevPosition = currPosition;
        currPosition = nextPosition;
        notifyPositionChanged();
    }

    private void notifyPositionChanged() {
        for (IObserver observer: observers) observer.changeSpritePosition(this);
    }

    private int[] createRotationPreferences(int[] currGenome) {
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
        for (int i = 0; i < splitIdx; i++)           newGenome[i] = genome1[i];
        for (int i = splitIdx; i < GENES_COUNT; i++) newGenome[i] = genome2[i];
        return newGenome;
    }

    private void decreaseEnergy(int deltaEnergy) {
        energy -= deltaEnergy;
    }

    public boolean canBreed() {
        return energy >= map.getMinBreedEnergy();
    }

    public Animal breed(Animal other) {
        // Do not bread if at least one of animals has not enough energy
        if (!canBreed() || !other.canBreed()) return null;
        // Calculate the energy lost by parents during reproduction
        int deltaEnergy1 = (int)(energy * BREED_ENERGY_LOSS_RATIO);
        int deltaEnergy2 = (int)(other.energy * BREED_ENERGY_LOSS_RATIO);
        // Decrease parents energy
        decreaseEnergy(deltaEnergy1);
        other.decreaseEnergy(deltaEnergy2);
        // Create the genome of the child
        int[] childGenome = mergeGenomes(genome, energy, other.genome, other.energy);
        // Create a new animal with the energy inherited from its parents
        Animal child = new Animal(map, currPosition, deltaEnergy1 + deltaEnergy2, childGenome);
        // Add a child to children lists of parents
        children.add(child);
        other.children.add(child);
        return child;
    }

    private long countDescendants(Animal animal) {
        long count = animal.children.size();
        for (Animal child: animal.children) {
            count += countDescendants(child);
        }
        return count;
    }
}
