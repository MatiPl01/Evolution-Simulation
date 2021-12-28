package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.IObserver;
import my.project.simulation.utils.Vector2D;

public interface ISprite {
    /**
     * Get String representation of an element,
     *
     * @return The string representing an element.
     */
    String toString();

    Vector2D getPosition();

    Vector2D getDisplayedPosition();

    IMap getMap();

    void add();

    void remove();

    /**
     * Add an object observing a map element
     *
     * @param observer
     *              Object that observes a map element
     */
    void addObserver(IObserver observer);

    /**
     * Remove an object observing a map element
     *
     * @param observer
     *              Object that observes a map element
     */
    void removeObserver(IObserver observer);

    void setGuiSprite(IGuiSprite guiSprite);

    IGuiSprite getGuiSprite();
}
