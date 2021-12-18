package my.project.simulation.sprites;

import my.project.simulation.utils.IObserver;
import my.project.simulation.maps.IMap;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSprite implements ISprite {
    protected final IMap map;
    protected final Set<IObserver> observers = new HashSet<>();

    protected AbstractSprite(IMap map) {
        this.map = map;
        addObserver((IObserver) map);
    }

    @Override
    public void remove() {
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
