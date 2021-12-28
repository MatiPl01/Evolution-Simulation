package my.project.simulation.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import my.project.simulation.sprites.Animal;
import my.project.simulation.stats.StatsMeter;

import java.util.ArrayList;
import java.util.List;

public class AnimalTracker {
    private final Animal animal;
    private final List<Animal> descendants = new ArrayList<>();
    private final Label trackedAnimalID;
    private final Label trackedAnimalChildren;
    private final Label trackedAnimalDescendants;
    private final Label trackedAnimalDeath;
    private int childrenCount = 0;

    public AnimalTracker(Animal animal, Label trackedAnimalID,
                                        Label trackedAnimalChildren,
                                        Label trackedAnimalDescendants,
                                        Label trackedAnimalDeath) {

        this.trackedAnimalID = trackedAnimalID;
        this.trackedAnimalChildren = trackedAnimalChildren;
        this.trackedAnimalDescendants = trackedAnimalDescendants;
        this.trackedAnimalDeath = trackedAnimalDeath;
        this.animal = animal;

        trackedAnimalID.setText(String.valueOf(animal.getID()));
        trackedAnimalChildren.setText("0");
        trackedAnimalDescendants.setText("0");
        trackedAnimalDeath.setText("-");
        animal.setTracker(this);
    }

    public Animal getTrackedAnimal() {
        return animal;
    }

    public void incrementChildrenCount() {
        childrenCount++;
        trackedAnimalChildren.setText(String.valueOf(childrenCount));
    }

    public void addToTrackedList(Animal descendant) {
        descendants.add(descendant);
        trackedAnimalDescendants.setText(String.valueOf(descendants.size()));
    }

    public void recordAnimalDeath() {
        trackedAnimalDeath.setText(String.valueOf(animal.getMap().getCurrentDayNum()));
    }

    public void remove() {
        animal.removeTracker();
        descendants.forEach(Animal::removeTracker);
        descendants.clear();

        trackedAnimalID.setText("-");
        trackedAnimalChildren.setText("-");
        trackedAnimalDescendants.setText("-");
        trackedAnimalDeath.setText("-");
    }
}
