package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import my.project.simulation.sprites.Animal;
import my.project.simulation.sprites.ISprite;

public class GuiAnimalSprite extends AbstractGuiSprite {
    public GuiAnimalSprite(ISprite sprite) {
        super(sprite);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) {
        updateAngle();
        if (sprite.getPosition() != ((Animal) sprite).getPrevPosition()) {
            gridBuilder.removeSprite(this);
            gridBuilder.addSprite(this);
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        vBox.setAlignment(Pos.BASELINE_CENTER);
        updateAngle();
    }

    private void updateAngle() {
        int angle = ((Animal) sprite).getDirection().getAngle();
        imageView.setRotate(angle);
    }
}
