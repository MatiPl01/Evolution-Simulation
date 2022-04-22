package my.project.simulation.utils;

import my.project.simulation.sprites.ISprite;

public interface IObserver {
    void changeSpritePosition(ISprite sprite);

    void addSprite(ISprite sprite);

    void removeSprite(ISprite sprite);
}
