package my.project.simulation.sprites;

import my.project.simulation.utils.IObserver;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSprite implements ISprite {
    protected final IMap map;
    protected final Set<IObserver> observers = new HashSet<>();
    protected Vector2D position;

    protected AbstractSprite(IMap map, Vector2D position) {
        this.map = map;
        this.position = position;
        // Add map observer
        addObserver((IObserver) map);
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public IMap getMap() {
        return map;
    }

    @Override
    public void remove() {
        System.out.println("<<>> Called remove()");
        for (IObserver observer: observers) observer.removeSprite(this);
    }

    @Override
    public void add() {
        for (IObserver observer: observers) observer.addSprite(this);
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
