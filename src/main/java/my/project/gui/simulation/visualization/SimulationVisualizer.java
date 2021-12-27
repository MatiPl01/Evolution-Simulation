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

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class SimulationVisualizer {
    private final IBuilder gridBuilder;
    private final IMap map;
    private boolean isPaused = false;
    private boolean isShowingDominantGenomesAnimals = false;

    public SimulationVisualizer(IMap map, AnchorPane parentContainer) throws IllegalArgumentException {
        this.map = map;
        if (map instanceof FoldingMap) {
            this.gridBuilder = new FoldingMapGridBuilder(map, parentContainer);
        } else if (map instanceof FencedMap) {
            this.gridBuilder = new FencedMapGridBuilder(map, parentContainer);
        } else throw new IllegalArgumentException("There is no GridBuilder defined for map: " + map.getClass());
    }

    public void startVisualization() {
        if (!isShowingDominantGenomesAnimals) isPaused = false;
        else {
            // TODO - display some message saying that there is no possibility to start animation when dominant genotypes animals are displayed
            System.out.println("Cannot start visualization as dominant genomes are shown");
        }
    }

    public void pauseVisualization() {
        isPaused = true;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void drawGrid() {
        gridBuilder.initialize();
        gridBuilder.render();
    }

    public void showNewFrame() {
        // TODO - add live data updates (charts, etc.)
        // (Don't implement any map sprites updates because they
        // will be updated automatically by their observers)
    }

    public void showDominantGenomesAnimals() {
        if (!isPaused) {
            // TODO - disable using this method in a gui when a simulation is not paused
            System.out.println("Cannot show animals with dominant genomes while simulation is running");
        } else {
            isShowingDominantGenomesAnimals = true;
            // Bring to top animals which have dominant genomes
            FutureTask<Void> future = new FutureTask<>(() -> {
                Set<Animal> animals = map.getDominantGenomesAnimals();
                bringAnimalsToTop(animals);
                setFocusOnAnimals(animals);
                showAnimalsIDs(animals);

                System.out.println("==== Dominant genomes ====");

                for (List<Integer> genome: map.getDominantGenomes()) {
                    // TODO - display genomes with animals IDs in GUI (instead of this code below)
                    System.out.println("Genome: " + genome);
                    for (Animal animal: map.getAnimalsWithGenome(genome)) {
                        System.out.println(" > " + animal.getID() + " at position: " + animal.getPosition());
                    }
                }
            }, null);

            Platform.runLater(future);

            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void hideDominantGenomesAnimals() {
        FutureTask<Void> future = new FutureTask<>(() -> {
            isShowingDominantGenomesAnimals = false;
            Set<Animal> animals = map.getDominantGenomesAnimals();
            bringAnimalsToTop(map.getMaxEnergyFieldAnimals());
            disableFocusOnAnimals(animals);
            hideAnimalsIDs(animals);
        }, null);

        Platform.runLater(future);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void bringAnimalsToTop(Set<Animal> animals) {
        for (Animal animal: animals) {
            IGuiSprite guiSprite = animal.getGuiSprite();
            gridBuilder.removeSprite(guiSprite);
            gridBuilder.addSprite(guiSprite);
        }
    }

    private void setFocusOnAnimals(Set<Animal> animals) {
        for (Animal animal: map.getAllAnimals()) {
            if (!animals.contains(animal)) {
                ((GuiAnimalSprite) animal.getGuiSprite()).setTransparent(true);
            }
        }
    }

    private void disableFocusOnAnimals(Set<Animal> animals) {
        for (Animal animal: map.getAllAnimals()) {
            if (!animals.contains(animal)) {
                ((GuiAnimalSprite) animal.getGuiSprite()).setTransparent(false);
            }
        }
    }

    private void showAnimalsIDs(Set<Animal> animals) {
        for (Animal animal: animals) ((GuiAnimalSprite) animal.getGuiSprite()).showID();
    }

    private void hideAnimalsIDs(Set<Animal> animals) {
        for (Animal animal: animals) ((GuiAnimalSprite) animal.getGuiSprite()).hideID();
    }
}
