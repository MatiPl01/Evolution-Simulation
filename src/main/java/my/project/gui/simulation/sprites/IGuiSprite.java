package my.project.gui.simulation.sprites;

import javafx.scene.Node;
import my.project.simulation.utils.Vector2D;

public interface IGuiSprite {
    Vector2D getPosition();

    Node getNode();

    void initialize();
}
