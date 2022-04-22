package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.IObserver;
import my.project.simulation.utils.Vector2D;

public interface ISprite {
    String toString();

    Vector2D getPosition();

    Vector2D getDisplayedPosition();

    IMap getMap();

    void add();

    void remove();

    void addObserver(IObserver observer);

    void removeObserver(IObserver observer);

    void setGuiSprite(IGuiSprite guiSprite);

    IGuiSprite getGuiSprite();
}
