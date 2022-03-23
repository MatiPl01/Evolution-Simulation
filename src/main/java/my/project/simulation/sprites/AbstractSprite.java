package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.IGuiSprite;
import my.project.simulation.utils.IObserver;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSprite implements ISprite {
    protected final IMap map;
    protected final Set<IObserver> observers = new HashSet<>();
    protected Vector2D position;
    private IGuiSprite guiSprite;

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
    public Vector2D getDisplayedPosition() {
        int width = map.getWidth();
        int height = map.getHeight();
        int x = position.getX() - width / 2;
        int y = position.getY() - height / 2;
        if (width % 2 == 0 && position.getX() >= width / 2) x += 1;
        if (height % 2 == 0 && position.getY() >= height / 2) y += 1;
        return new Vector2D(x, y);
    }

    @Override
    public IMap getMap() {
        return map;
    }

    @Override
    public void remove() { for (IObserver observer: observers) observer.removeSprite(this); }

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

    @Override
    public void setGuiSprite(IGuiSprite guiSprite) {
        this.guiSprite = guiSprite;
    }

    @Override
    public IGuiSprite getGuiSprite() {
        return guiSprite;
    }
}
