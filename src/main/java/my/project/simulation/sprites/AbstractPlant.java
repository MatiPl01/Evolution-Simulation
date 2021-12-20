package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.GuiPlantSprite;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public abstract class AbstractPlant extends AbstractSprite {
    protected final int energy;
    protected final String IMG_PATH;

    public AbstractPlant(IMap map, Vector2D position, int energy) {
        super(map, position);
        this.energy = energy;
        this.IMG_PATH = getRandomImagePath();
        addObserver(new GuiPlantSprite(this));
    }

    @Override
    public String getImagePath() {
        return IMG_PATH;
    }

    public int getEnergy() {
        return energy;
    }

    abstract String getRandomImagePath();
}
