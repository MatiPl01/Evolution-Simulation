package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import my.project.simulation.sprites.Animal;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.AnimalTracker;

public class GuiAnimalSprite extends AbstractGuiSprite {
    private ProgressBar energyBar;

    public GuiAnimalSprite(ISprite sprite) {
        super(sprite);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) {
        updateAngle();
        updateEnergyBar();
        if (sprite.getPosition() != ((Animal) sprite).getPrevPosition()) {
            gridBuilder.removeSprite(this);
            gridBuilder.addSprite(this);
        }
    }

    @Override
    public void initialize() {
        energyBar = new ProgressBar(1);
        energyBar.setPrefWidth(gridBuilder.getCellSize());
        energyBar.setPrefHeight(4);
        Group group = new Group(energyBar);
        vBox.getChildren().add(group);
        super.initialize();
        vBox.setAlignment(Pos.BASELINE_CENTER);
        energyBar.setProgress(.5);
        updateAngle();
        updateEnergyBar();
    }

    private void updateAngle() {
        int angle = ((Animal) sprite).getDirection().getAngle();
        imageView.setRotate(angle);
    }

    private double getEnergyRatio() {
        int startEnergy = sprite.getMap().getStartEnergy();
        int currEnergy = ((Animal) sprite).getEnergy();
        return Math.min(1. * currEnergy / startEnergy, 1);
    }

    private String getHealthBarColor() {
        double energyRatio = getEnergyRatio();
        if (energyRatio > .5) return "rgb(" + (int)((1 - energyRatio) * 2 * 255) + ", 255, 0)";
        else return "rgb(255," + (int)(energyRatio * 2 * 255) + ", 0)";
    }

    private void updateEnergyBar() {
        energyBar.setProgress(getEnergyRatio());
        energyBar.setStyle("-fx-accent: " + getHealthBarColor());
    }

    // TODO - add some event which fires this method
    public void setupAnimalTracker() {
        sprite.getMap().setAnimalTracker(new AnimalTracker((Animal) sprite));
    }
}
