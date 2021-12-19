package my.project.simulation.sprites;

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

    /**
     * Get a path to the image representing a map element
     *
     * @return The string containing a path to the image
     */
    String getImagePath();

    Vector2D getPosition();

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
}
