package my.project.simulation.utils;

import my.project.simulation.sprites.Animal;

import java.util.Comparator;

public class MaxEnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        // If animals have the same energy and are different animals, return
        // something different to 0 in order to add both animals to the Set
        if (o1.getEnergy() == o2.getEnergy()) return o1.getID() == o2.getID() ? 0 : 1;
        // Otherwise, sort animals in a non-increasing order
        return o2.getEnergy() - o1.getEnergy();
    }
}
