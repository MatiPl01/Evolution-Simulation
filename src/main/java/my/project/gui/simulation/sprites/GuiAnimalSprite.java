package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import my.project.simulation.sprites.ISprite;

public class GuiAnimalSprite extends AbstractGuiSprite {
    public GuiAnimalSprite(ISprite sprite) {
        super(sprite);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) {
        // TODO - add rotation handling and position changes
    }

    @Override
    public void initialize() {
        super.initialize();
        vBox.setAlignment(Pos.BASELINE_CENTER);
    }
}
