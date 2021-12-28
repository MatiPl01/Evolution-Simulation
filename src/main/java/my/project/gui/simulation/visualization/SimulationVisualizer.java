package my.project.gui.simulation.visualization;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import my.project.gui.simulation.grid.FencedMapGridBuilder;
import my.project.gui.simulation.grid.FoldingMapGridBuilder;
import my.project.gui.simulation.grid.IBuilder;
import my.project.gui.simulation.sprites.GuiAnimalSprite;
import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.maps.FencedMap;
import my.project.simulation.maps.FoldingMap;
import my.project.simulation.maps.IMap;
import my.project.simulation.sprites.Animal;

import java.util.Set;

public class SimulationVisualizer {
    private final IBuilder gridBuilder;
    private final IMap map;

    public SimulationVisualizer(IMap map, AnchorPane parentContainer) throws IllegalArgumentException {
        this.map = map;
        if (map instanceof FoldingMap) {
            this.gridBuilder = new FoldingMapGridBuilder(map, parentContainer);
        } else if (map instanceof FencedMap) {
            this.gridBuilder = new FencedMapGridBuilder(map, parentContainer);
        } else throw new IllegalArgumentException("There is no GridBuilder defined for map: " + map.getClass());
    }

    public void drawGrid() {
        gridBuilder.initialize();
        gridBuilder.render();
    }

    public void showDominantGenomesAnimals() {
        Platform.runLater(() -> {
            Set<Animal> animals = map.getDominantGenomesAnimals();
            bringAnimalsToTop(animals);
            setFocusOnAnimals(animals);
            showAnimalsIDs(animals);
        });
    }

    public void hideDominantGenomesAnimals() {
        Platform.runLater(() -> {
            Set<Animal> animals = map.getDominantGenomesAnimals();
            bringAnimalsToTop(map.getMaxEnergyFieldAnimals());
            disableFocusOnAnimals(animals);
            hideAnimalsIDs(animals);
        });
    }

    public void bringAnimalsToTop(Set<Animal> animals) {
        for (Animal animal: animals) {
            animal.getGuiSprite().getNode().toFront();
        }
    }

    public void setFocusOnAnimals(Set<Animal> animals) {
        for (Animal animal: map.getAllAnimals()) {
            if (!animals.contains(animal)) {
                ((GuiAnimalSprite) animal.getGuiSprite()).setTransparent(true);
            }
        }
    }

    public void disableFocusOnAnimals(Set<Animal> animals) {
        for (Animal animal: map.getAllAnimals()) {
            if (!animals.contains(animal)) {
                ((GuiAnimalSprite) animal.getGuiSprite()).setTransparent(false);
            }
        }
    }

    public void showAnimalsIDs(Set<Animal> animals) {
        for (Animal animal: animals) ((GuiAnimalSprite) animal.getGuiSprite()).showID();
    }

    public void hideAnimalsIDs(Set<Animal> animals) {
        for (Animal animal: animals) ((GuiAnimalSprite) animal.getGuiSprite()).hideID();
    }
}
