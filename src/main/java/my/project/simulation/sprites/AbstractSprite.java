package my.project.simulation.sprites;

import my.project.simulation.IObserver;
import my.project.simulation.utils.Vector2D;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSprite implements ISprite {
    protected Vector2D currPosition;
    protected final Set<IObserver> observers = new HashSet<>();

    public AbstractSprite(Vector2D position) {
        this.currPosition = position;
    }

    @Override
    public Vector2D getPosition() {
        return currPosition;
    }

    @Override
    public void remove() {
        observers.forEach(IObserver::removedSprite);
    }

    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }
}
