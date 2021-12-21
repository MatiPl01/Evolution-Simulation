package my.project.simulation.utils;

import my.project.simulation.sprites.Animal;
import my.project.simulation.stats.StatsMeter;

import java.util.ArrayList;
import java.util.List;

public class AnimalTracker {
    private final Animal animal;
    private final List<Animal> descendants = new ArrayList<>();
    private int childrenCount = 0;
    private StatsMeter statsMeter;

    public AnimalTracker(Animal animal) {
        this.animal = animal;
        animal.setTracker(this);
    }

    public void setStatsMeter(StatsMeter statsMeter) {
        this.statsMeter = statsMeter;
    }

    public Animal getTrackedAnimal() {
        return animal;
    }

    public void recordAnimalDeath() {
        statsMeter.updateTrackedAnimalDeath(animal.getMap().getCurrentDayNum());
    }

    public void addToTrackedList(Animal descendant) {
        descendants.add(descendant);
        statsMeter.updateTrackedAnimalDescendants(descendants.size());
    }

    public void incrementChildrenCount() {
        childrenCount++;
        statsMeter.updateTrackedAnimalChildren(childrenCount);
    }

    public void remove() {
        animal.removeTracker();
        descendants.forEach(Animal::removeTracker);
        descendants.clear();
    }
}
