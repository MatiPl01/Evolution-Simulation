package my.project.simulation.utils;

import my.project.simulation.sprites.Animal;

import java.util.Comparator;

public class EnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        return o2.getEnergy() - o1.getEnergy();
    }
}
