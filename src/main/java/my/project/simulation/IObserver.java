package my.project.simulation;

import my.project.simulation.sprites.ISprite;

public interface IObserver {
    /**
     * A method which allows an observed element to send notification
     * to its observer after changing position
     *
     * @return
     *      A map element which position was changed
     */
    ISprite changedSpritePosition();

    /**
     * A method which allows an observed element to send notification
     * to its observer after being spawned on the map
     *
     * @return
     */
    ISprite spawnedSprite();

    /**
     * A method which allows an observed element to send notification
     * to its observer after being removed from the map
     *
     * @return
     */
    ISprite removedSprite();
}
