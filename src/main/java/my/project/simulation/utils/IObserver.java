package my.project.simulation.utils;

import my.project.simulation.sprites.ISprite;

public interface IObserver {
    /**
     * A method which allows an observed element to send notification
     * to its observer after changing position
     */
    void changeSpritePosition(ISprite sprite);

    /**
     * A method which allows an observed element to send notification
     * to its observer after being spawned on the map
     */
    void addSprite(ISprite sprite);

    /**
     * A method which allows an observed element to send notification
     * to its observer after being removed from the map
     */
    void removeSprite(ISprite sprite);
}
