package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import my.project.simulation.sprites.ISprite;

public class GuiPlantSprite extends AbstractGuiSprite {
    public GuiPlantSprite(ISprite sprite) {
        super(sprite);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) {
        // Do nothing
    }

    @Override
    public void initialize() {
        super.initialize();
        mainBox.setAlignment(Pos.BOTTOM_CENTER);
    }
}
