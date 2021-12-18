package my.project.simulation.utils;

import my.project.simulation.sprites.Animal;

import java.util.Comparator;

public class EnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        // If animals have the same energy, return something
        // different to 0 in order to add both animals to the Set
        if (o1.getEnergy() == o2.getEnergy()) return 1;
        // Otherwise, sort animals in a non-increasing order
        return o2.getEnergy() - o1.getEnergy();
    }
}
