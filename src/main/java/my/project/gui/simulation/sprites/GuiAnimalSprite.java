package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import my.project.simulation.sprites.Animal;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.AnimalTracker;

public class GuiAnimalSprite extends AbstractGuiSprite {
    private static final double OPACITY = .3;
    private static final double ID_FONT_SIZE = 14;
    private static final String ID_BOX_CLASS = "animalIDBox";

    private ProgressBar energyBar;
    private VBox overlayBox;

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
        // Add an energy bar at the top of the vBox
        energyBar = new ProgressBar(1);
        energyBar.setPrefWidth(gridBuilder.getCellSize());
        energyBar.setPrefHeight(4);
        Group group = new Group(energyBar);
        mainBox.getChildren().add(group);
        // Initialize AbstractGuiSprite class
        super.initialize();
        // Update animal visual state
        mainBox.setAlignment(Pos.BASELINE_CENTER);
        energyBar.setProgress(.5);
        updateAngle();
        updateEnergyBar();
    }

    // TODO - add some event which fires this method
    public void setupAnimalTracker() {
        sprite.getMap().setAnimalTracker(new AnimalTracker((Animal) sprite));
    }

    public void setTransparent(boolean isSet) {
        if (isSet) mainBox.setOpacity(OPACITY);
        else mainBox.setOpacity(1);
    }

    public void showID() {
        // Create the ID label if it hasn't been created before
        if (overlayBox == null) {
            overlayBox = new VBox();
            overlayBox.setAlignment(Pos.BOTTOM_LEFT);
            Label idLabel = new Label(String.valueOf(((Animal) sprite).getID()));
            idLabel.setFont(new Font(ID_FONT_SIZE));
            VBox idBox = new VBox(idLabel);
            idBox.setAlignment(Pos.TOP_CENTER);
            idBox.getStyleClass().add(ID_BOX_CLASS);
            idBox.setMaxWidth(ID_FONT_SIZE * 2);
            idBox.setMaxHeight(ID_FONT_SIZE * 2);
            overlayBox.getChildren().add(idBox);
        }
        spriteBox.getChildren().add(overlayBox);
    }

    public void hideID() {
        spriteBox.getChildren().remove(overlayBox);
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
}
