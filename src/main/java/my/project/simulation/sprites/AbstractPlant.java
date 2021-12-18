package my.project.simulation.sprites;

import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public abstract class AbstractPlant extends AbstractSprite {
    protected final int energy;
    protected final String IMG_PATH;
    protected final Vector2D position;

    public AbstractPlant(IMap map, Vector2D position, int energy) {
        super(map);
        this.energy = energy;
        this.position = position;
        this.IMG_PATH = getRandomImagePath();
    }

    @Override
    public String getImagePath() {
        return IMG_PATH;
    }

    public Vector2D getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    abstract String getRandomImagePath();
}
